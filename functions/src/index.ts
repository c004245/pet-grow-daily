/**
 * Import function triggers from their respective submodules:
 *
 * import {onCall} from "firebase-functions/v2/https";
 * import {onDocumentWritten} from "firebase-functions/v2/firestore";
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

// 환경변수 로드 (가장 먼저 실행되어야 함)
import * as dotenv from "dotenv";
dotenv.config();

import {setGlobalOptions} from "firebase-functions";
import {onCall} from "firebase-functions/v2/https";
import * as logger from "firebase-functions/logger";
import * as admin from "firebase-admin";
import axios from "axios";
import archiver from "archiver";

// Firebase Admin 초기화
admin.initializeApp();

// Start writing functions
// https://firebase.google.com/docs/functions/typescript

// For cost control, you can set the maximum number of containers that can be
// running at the same time. This helps mitigate the impact of unexpected
// traffic spikes by instead downgrading performance. This limit is a
// per-function limit. You can override the limit for each function using the
// `maxInstances` option in the function's options, e.g.
// `onRequest({ maxInstances: 5 }, (req, res) => { ... })`.
// NOTE: setGlobalOptions does not apply to functions using the v1 API. V1
// functions should each use functions.runWith({ maxInstances: 10 }) instead.
// In the v1 API, each function can only serve one request per container, so
// this will be the maximum concurrent request count.
setGlobalOptions({ maxInstances: 10 });

// ZIP 파일 생성 Function
export const generateAlbumZipfile = onCall({
  enforceAppCheck: false, // AppCheck 비활성화
  timeoutSeconds: 540, // 9분으로 타임아웃 증가
  memory: "1GiB", // 메모리를 1GB로 증가
}, async (request) => {
  try {
    const { orderId, userId } = request.data;
    
    // 파라미터 검증
    if (!orderId || !userId) {
      throw new Error('orderId와 userId가 필요합니다.');
    }

    logger.info(`ZIP 파일 생성 시작 - OrderId: ${orderId}, UserId: ${userId}`);

    // 주문 데이터 조회
    const orderDoc = await admin.firestore()
      .collection('users')
      .doc(userId.toString())
      .collection('orders')
      .doc(orderId)
      .get();

    if (!orderDoc.exists) {
      throw new Error('주문을 찾을 수 없습니다.');
    }

    const orderData = orderDoc.data()!;
    const { selectedImages } = orderData;

    if (!selectedImages || selectedImages.length === 0) {
      throw new Error('선택된 이미지가 없습니다.');
    }

    logger.info(`선택된 이미지 개수: ${selectedImages.length}`);

    // ZIP 파일 생성
    const zipBuffer = await createImageZip(selectedImages, orderId);

    // Storage에 ZIP 업로드
    const bucket = admin.storage().bucket();
    const zipFileName = `${orderId}.zip`;
    const fileName = `orders/${zipFileName}`;
    const file = bucket.file(fileName);

    await file.save(zipBuffer, {
      metadata: {
        contentType: 'application/zip',
        contentDisposition: 'attachment',
        cacheControl: 'no-cache',
      },
      resumable: false,
      validation: false,
    });

    // 공개 URL 생성
    await file.makePublic();
    
    // 파일 메타데이터 재설정 (Firebase Console 호환성)
    await file.setMetadata({
      contentType: 'application/zip',
      contentDisposition: 'attachment',
      cacheControl: 'no-cache',
      metadata: {
        firebaseStorageDownloadTokens: 'public'
      }
    });

    const [url] = await file.getSignedUrl({
      action: 'read',
      expires: Date.now() + 24 * 60 * 60 * 1000, // 24시간
    });
    const zipUrl = url;

    // Firestore에 ZIP URL 업데이트
    await orderDoc.ref.update({
      zipUrl: zipUrl,
      zipGeneratedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    logger.info(`ZIP 파일 생성 완료 - URL: ${zipUrl}`);

    return {
      success: true,
      zipUrl: zipUrl,
      imageCount: selectedImages.length,
      message: 'ZIP 파일 생성이 완료되었습니다.'
    };

  } catch (error) {
    logger.error('ZIP 파일 생성 실패:', error);
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';
    throw new Error(`ZIP 파일 생성에 실패했습니다: ${errorMessage}`);
  }
});

// ZIP 파일 생성 함수
async function createImageZip(
  imageUrls: string[],
  orderId: string
): Promise<Buffer> {
  return new Promise(async (resolve, reject) => {
    try {
      const archive = archiver('zip', {
        zlib: { level: 6 } // 압축 레벨을 낮춰서 속도 향상
      });

      const chunks: Buffer[] = [];

      archive.on('data', (chunk) => chunks.push(chunk));
      archive.on('end', () => resolve(Buffer.concat(chunks)));
      archive.on('error', (error) => reject(error));

      logger.info(`총 ${imageUrls.length}개 이미지 처리 시작`);

      // 배치 크기 (한 번에 처리할 이미지 수)
      const BATCH_SIZE = 5;
      let processedCount = 0;
      let successCount = 0;
      let failCount = 0;

      // 배치별로 처리
      for (let batchStart = 0; batchStart < imageUrls.length; batchStart += BATCH_SIZE) {
        const batchEnd = Math.min(batchStart + BATCH_SIZE, imageUrls.length);
        const batch = imageUrls.slice(batchStart, batchEnd);
        
        logger.info(`배치 처리 중: ${batchStart + 1}-${batchEnd}/${imageUrls.length}`);

        // 배치 내 이미지들을 병렬 처리
        const batchPromises = batch.map(async (imageUrl, batchIndex) => {
          const globalIndex = batchStart + batchIndex;
          
          try {
            const response = await axios.get(imageUrl, { 
              responseType: 'arraybuffer',
              timeout: 45000, // 45초 타임아웃
              headers: {
                'User-Agent': 'Pet-Grow-Daily-ZIP-Generator'
              },
              maxContentLength: 50 * 1024 * 1024, // 50MB 제한
              maxBodyLength: 50 * 1024 * 1024
            });
            
            const imageBuffer = Buffer.from(response.data);
            
            // 파일 확장자 추출
            const urlParts = imageUrl.split('.');
            const extension = urlParts[urlParts.length - 1].split('?')[0];
            
            // ZIP에 파일 추가
            const filename = `${orderId}_image_${String(globalIndex + 1).padStart(3, '0')}.${extension}`;
            archive.append(imageBuffer, { name: filename });
            
            logger.info(`이미지 ${globalIndex + 1} 처리 완료: ${filename}`);
            return { success: true, index: globalIndex + 1 };
            
          } catch (imageError) {
            logger.error(`이미지 ${globalIndex + 1} 처리 실패: ${imageUrl}`, imageError);
            return { success: false, index: globalIndex + 1, error: imageError };
          }
        });

        // 배치 결과 대기
        const batchResults = await Promise.allSettled(batchPromises);
        
        // 결과 집계
        batchResults.forEach((result, index) => {
          processedCount++;
          if (result.status === 'fulfilled' && result.value.success) {
            successCount++;
          } else {
            failCount++;
            logger.warn(`배치 ${batchStart + index + 1} 처리 실패`);
          }
        });

        logger.info(`배치 처리 완료. 성공: ${successCount}, 실패: ${failCount}, 전체: ${processedCount}/${imageUrls.length}`);

        // 메모리 정리를 위한 짧은 대기
        if (batchStart + BATCH_SIZE < imageUrls.length) {
          await new Promise(resolve => setTimeout(resolve, 100));
        }
      }

      logger.info(`모든 이미지 처리 완료. 성공: ${successCount}, 실패: ${failCount}`);

      // 모든 이미지가 성공적으로 처리되지 않으면 실패로 간주
      if (successCount !== imageUrls.length) {
        throw new Error(`모든 이미지가 처리되어야 합니다 (성공: ${successCount}/${imageUrls.length})`);
      }

      // ZIP 파일 완료
      archive.finalize();

    } catch (error) {
      logger.error('ZIP 생성 중 오류:', error);
      reject(error);
    }
  });
}

// 푸시 알림 전송 Function
export const sendPushNotification = onCall({
  enforceAppCheck: false, // AppCheck 비활성화
  timeoutSeconds: 60, // 1분 타임아웃
}, async (request) => {
  try {
    const { type, title, body, targetType, targetValue } = request.data;
    
    // 파라미터 검증
    if (!type || !title || !body) {
      throw new Error('type, title, body는 필수 파라미터입니다.');
    }
    
    // 지원되는 알림 타입 확인
    const validTypes = ['marketing', 'delivery', 'system'];
    if (!validTypes.includes(type)) {
      throw new Error(`지원되지 않는 알림 타입입니다. 지원 타입: ${validTypes.join(', ')}`);
    }

    logger.info(`푸시 알림 전송 시작 - Type: ${type}, Title: ${title}`);

    let result;

    // targetType이 없거나 비어있으면 무조건 전체 발송 ('all')
    if (!targetType || targetType === '') {
      // 모든 사용자에게 전송 (토픽 사용)
      const topicMessage: admin.messaging.TopicMessage = {
        topic: 'all_users',
        data: {
          type: type,
          title: title,
          body: body,
        },
      };
      result = await admin.messaging().send(topicMessage);
      logger.info(`모든 사용자에게 알림 전송 완료 (targetType 없음) - MessageId: ${result}`);
    } else {
      // targetType이 있는 경우 기존 로직 사용
      switch (targetType) {
        case 'all':
          // 모든 사용자에게 전송 (토픽 사용)
          const topicMessage: admin.messaging.TopicMessage = {
            topic: 'all_users',
            data: {
              type: type,
              title: title,
              body: body,
            },
          };
          result = await admin.messaging().send(topicMessage);
          logger.info(`모든 사용자에게 알림 전송 완료 - MessageId: ${result}`);
          break;
          
        case 'token':
          // 특정 토큰에 전송
          if (!targetValue) {
            throw new Error('targetValue(FCM 토큰)가 필요합니다.');
          }
          const tokenMessage: admin.messaging.TokenMessage = {
            token: targetValue,
            data: {
              type: type,
              title: title,
              body: body,
            },
          };
          result = await admin.messaging().send(tokenMessage);
          logger.info(`특정 토큰에 알림 전송 완료 - MessageId: ${result}`);
          break;
          
        case 'user':
          // 특정 사용자에게 전송 (사용자의 FCM 토큰 조회 필요)
          if (!targetValue) {
            throw new Error('targetValue(사용자 ID)가 필요합니다.');
          }
          
          // Firestore에서 사용자의 FCM 토큰 조회
          const userDoc = await admin.firestore()
            .collection('users')
            .doc(targetValue)
            .get();
            
          if (!userDoc.exists) {
            throw new Error('사용자를 찾을 수 없습니다.');
          }
          
          const userData = userDoc.data();
          const fcmToken = userData?.fcmToken;
          
          if (!fcmToken) {
            throw new Error('사용자의 FCM 토큰이 없습니다.');
          }
          
          const userTokenMessage: admin.messaging.TokenMessage = {
            token: fcmToken,
            data: {
              type: type,
              title: title,
              body: body,
            },
          };
          result = await admin.messaging().send(userTokenMessage);
          logger.info(`특정 사용자에게 알림 전송 완료 - UserId: ${targetValue}, MessageId: ${result}`);
          break;
          
        default:
          throw new Error('지원되지 않는 targetType입니다. (all, token, user)');
      }
    }

    return {
      success: true,
      messageId: result,
      type: type,
      targetType: targetType || 'all',
      message: '푸시 알림 전송이 완료되었습니다.'
    };

  } catch (error) {
    logger.error('푸시 알림 전송 실패:', error);
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';
    throw new Error(`푸시 알림 전송에 실패했습니다: ${errorMessage}`);
  }
});

// 포트원 액세스 토큰 획득
async function getPortoneAccessToken(): Promise<string> {
  try {
    const response = await axios.post('https://api.iamport.kr/users/getToken', {
      imp_key: process.env.PORTONE_REST_API_KEY,
      imp_secret: process.env.PORTONE_REST_API_SECRET,
    });

    if (response.data.code !== 0) {
      throw new Error(`포트원 인증 실패: ${response.data.message}`);
    }

    return response.data.response.access_token;
  } catch (error) {
    logger.error('포트원 액세스 토큰 획득 실패:', error);
    throw new Error('포트원 인증에 실패했습니다.');
  }
}

// 포트원 결제 정보 조회
async function getPortonePayment(accessToken: string, impUid: string) {
  try {
    const response = await axios.get(`https://api.iamport.kr/payments/${impUid}`, {
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    if (response.data.code !== 0) {
      throw new Error(`결제 정보 조회 실패: ${response.data.message}`);
    }

    return response.data.response;
  } catch (error) {
    logger.error('포트원 결제 정보 조회 실패:', error);
    throw new Error('결제 정보 조회에 실패했습니다.');
  }
}

// 포트원 결제 취소/환불 함수
async function cancelPortonePayment(accessToken: string, impUid: string, reason: string = "결제 검증 실패") {
  try {
    logger.info(`결제 취소 요청 시작 - imp_uid: ${impUid}, reason: ${reason}`);
    
    const response = await axios.post('https://api.iamport.kr/payments/cancel', {
      imp_uid: impUid,
      reason: reason,
      amount: undefined, // 전액 취소
      checksum: undefined // 전액 취소 시 불필요
    }, {
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    if (response.data.code !== 0) {
      throw new Error(`결제 취소 실패: ${response.data.message}`);
    }

    logger.info('결제 취소 성공:', {
      imp_uid: impUid,
      cancel_amount: response.data.response.cancel_amount,
      status: response.data.response.status
    });

    return response.data.response;
  } catch (error) {
    logger.error('결제 취소 실패:', error);
    throw new Error(`결제 취소 처리에 실패했습니다: ${error instanceof Error ? error.message : 'Unknown error'}`);
  }
}

// 결제 검증 Function
export const verifyPayment = onCall({
  enforceAppCheck: false,
  timeoutSeconds: 60,
}, async (request) => {
  try {
    const { impUid, merchantUid, expectedAmount, userId } = request.data;

    logger.info('=== 결제 검증 시작 ===');
    logger.info('요청 파라미터:', { impUid, merchantUid, expectedAmount, userId });

    // 파라미터 검증
    if (!impUid || !merchantUid || !expectedAmount || !userId) {
      const missingParams = [];
      if (!impUid) missingParams.push('impUid');
      if (!merchantUid) missingParams.push('merchantUid');
      if (!expectedAmount) missingParams.push('expectedAmount');
      if (!userId) missingParams.push('userId');
      
      const errorMsg = `필수 파라미터가 누락되었습니다: ${missingParams.join(', ')}`;
      logger.error(errorMsg);
      throw new Error(errorMsg);
    }

    logger.info(`결제 검증 시작 - imp_uid: ${impUid}, merchant_uid: ${merchantUid}, expected_amount: ${expectedAmount}, user_id: ${userId}`);

    // 1. 포트원에서 실제 결제 정보 조회
    logger.info('1단계: 포트원 액세스 토큰 획득 시작');
    const accessToken = await getPortoneAccessToken();
    logger.info('1단계: 포트원 액세스 토큰 획득 완료');

    logger.info('2단계: 포트원 결제 정보 조회 시작');
    const paymentData = await getPortonePayment(accessToken, impUid);
    logger.info('2단계: 포트원 결제 정보 조회 완료');

    logger.info('포트원에서 조회된 결제 정보:', {
      status: paymentData.status,
      amount: paymentData.amount,
      merchant_uid: paymentData.merchant_uid,
      pg_provider: paymentData.pg_provider,
      pay_method: paymentData.pay_method,
      paid_at: paymentData.paid_at,
      pg_tid: paymentData.pg_tid
    });

    // 2. 결제 상태 검증
    logger.info('3단계: 결제 상태 검증 시작');
    if (paymentData.status !== 'paid') {
      const errorMsg = `결제가 완료되지 않았습니다. 상태: ${paymentData.status}`;
      logger.error('3단계 실패:', errorMsg);
      throw new Error(errorMsg);
    }
    logger.info('3단계: 결제 상태 검증 완료 (상태: paid)');

    // 3. 결제 금액 검증
    logger.info('4단계: 결제 금액 검증 시작');
    const expectedAmountInt = parseInt(expectedAmount);
    logger.info('금액 비교:', {
      expected: expectedAmountInt,
      actual: paymentData.amount,
      expectedType: typeof expectedAmountInt,
      actualType: typeof paymentData.amount
    });
    
    if (paymentData.amount !== expectedAmountInt) {
      const errorMsg = `결제 금액이 일치하지 않습니다. 예상: ${expectedAmount}(${expectedAmountInt}), 실제: ${paymentData.amount}`;
      logger.error('4단계 실패:', errorMsg);
      throw new Error(errorMsg);
    }
    logger.info('4단계: 결제 금액 검증 완료');

    // 4. 주문번호 검증
    logger.info('5단계: 주문번호 검증 시작');
    logger.info('주문번호 비교:', {
      expected: merchantUid,
      actual: paymentData.merchant_uid
    });
    
    if (paymentData.merchant_uid !== merchantUid) {
      const errorMsg = `주문번호가 일치하지 않습니다. 예상: ${merchantUid}, 실제: ${paymentData.merchant_uid}`;
      logger.error('5단계 실패:', errorMsg);
      throw new Error(errorMsg);
    }
    logger.info('5단계: 주문번호 검증 완료');

    // 5. 중복 결제 검증 (Firestore에서 이미 처리된 결제인지 확인)
    logger.info('6단계: 중복 결제 검증 시작');
    const existingPayment = await admin.firestore()
      .collection('payments')
      .doc(impUid)
      .get();

    if (existingPayment.exists) {
      const existingData = existingPayment.data();
      logger.info('기존 결제 기록 발견:', {
        impUid,
        existingStatus: existingData?.status,
        existingUserId: existingData?.userId,
        existingVerifiedAt: existingData?.verifiedAt
      });
      
      if (existingData?.status === 'verified') {
        const errorMsg = '이미 처리된 결제입니다.';
        logger.error('6단계 실패:', errorMsg);
        throw new Error(errorMsg);
      }
    } else {
      logger.info('새로운 결제 - 기존 기록 없음');
    }
    logger.info('6단계: 중복 결제 검증 완료');

    // 6. 결제 검증 완료 - Firestore에 결제 정보 저장
    logger.info('7단계: Firestore에 결제 검증 결과 저장 시작');
    const paymentRecord = {
      impUid,
      merchantUid,
      amount: paymentData.amount,
      status: 'verified',
      userId,
      verifiedAt: admin.firestore.FieldValue.serverTimestamp(),
      paymentData: {
        pg_provider: paymentData.pg_provider,
        pg_tid: paymentData.pg_tid,
        pay_method: paymentData.pay_method,
        paid_at: paymentData.paid_at,
      },
    };

    await admin.firestore()
      .collection('payments')
      .doc(impUid)
      .set(paymentRecord);
    
    logger.info('7단계: Firestore에 결제 검증 결과 저장 완료');
    logger.info('저장된 결제 기록:', paymentRecord);

    logger.info(`=== 결제 검증 성공 완료 - imp_uid: ${impUid} ===`);

    const successResult = {
      success: true,
      verified: true,
      impUid,
      merchantUid,
      amount: paymentData.amount,
      paidAt: paymentData.paid_at,
      message: '결제 검증이 완료되었습니다.',
    };

    logger.info('클라이언트로 반환할 성공 결과:', successResult);
    return successResult;

  } catch (error) {
    logger.error('=== 결제 검증 실패 ===');
    logger.error('오류 내용:', error);
    logger.error('오류 스택:', error instanceof Error ? error.stack : 'No stack trace');
    
    const { impUid, merchantUid, userId } = request.data;
    
    // 검증 실패 기록
    if (impUid) {
      logger.info('검증 실패 기록을 Firestore에 저장 시작');
      const failureRecord = {
        impUid,
        merchantUid,
        userId,
        status: 'verification_failed',
        error: error instanceof Error ? error.message : 'Unknown error',
        failedAt: admin.firestore.FieldValue.serverTimestamp(),
        refundStatus: 'pending', // 환불 상태 추가
      };

      await admin.firestore()
        .collection('payments')
        .doc(impUid)
        .set(failureRecord, { merge: true });
      
      logger.info('검증 실패 기록 저장 완료:', failureRecord);
    }

    // 포트원 결제 취소/환불 처리
    let refundSuccess = false;
    if (impUid) {
      try {
        logger.info('포트원 결제 취소/환불 처리 시작');
        const accessToken = await getPortoneAccessToken();
        await cancelPortonePayment(accessToken, impUid, `결제 검증 실패: ${error instanceof Error ? error.message : 'Unknown error'}`);
        
        // 환불 성공 시 Firestore 업데이트
        await admin.firestore()
          .collection('payments')
          .doc(impUid)
          .update({
            refundStatus: 'completed',
            refundedAt: admin.firestore.FieldValue.serverTimestamp(),
          });
        
        refundSuccess = true;
        logger.info('포트원 결제 취소/환불 처리 완료');
      } catch (refundError) {
        logger.error('결제 취소/환불 처리 실패:', refundError);
        
        // 환불 실패 시 Firestore 업데이트
        await admin.firestore()
          .collection('payments')
          .doc(impUid)
          .update({
            refundStatus: 'failed',
            refundError: refundError instanceof Error ? refundError.message : 'Unknown error',
            refundFailedAt: admin.firestore.FieldValue.serverTimestamp(),
          });

        // 관리자에게 긴급 알림 전송
        try {
          await sendAdminAlert({
            type: 'payment_verification_failed_refund_failed',
            title: '🚨 긴급: 결제 검증 실패 + 환불 실패',
            message: `imp_uid: ${impUid}\n검증 실패: ${error instanceof Error ? error.message : 'Unknown error'}\n환불 실패: ${refundError instanceof Error ? refundError.message : 'Unknown error'}\n즉시 수동 환불 처리 필요`,
            impUid,
            userId: userId?.toString() || 'unknown',
          });
        } catch (alertError) {
          logger.error('관리자 알림 전송 실패:', alertError);
        }
      }
    }

    // 환불 성공 시에도 관리자에게 알림 (모니터링 목적)
    if (refundSuccess) {
      try {
        await sendAdminAlert({
          type: 'payment_verification_failed_refund_success',
          title: '⚠️ 결제 검증 실패 (자동 환불 완료)',
          message: `imp_uid: ${impUid}\n검증 실패 사유: ${error instanceof Error ? error.message : 'Unknown error'}\n자동 환불 처리 완료`,
          impUid,
          userId: userId?.toString() || 'unknown',
        });
      } catch (alertError) {
        logger.error('관리자 알림 전송 실패:', alertError);
      }
    }

    const errorMessage = error instanceof Error ? error.message : 'Unknown error';
    const failureResult = {
      success: false,
      verified: false,
      error: errorMessage,
      message: refundSuccess ? '결제 검증에 실패하여 자동으로 환불 처리되었습니다.' : '결제 검증에 실패했습니다. 고객센터에 문의해주세요.',
      refundProcessed: refundSuccess,
    };

    logger.info('클라이언트로 반환할 실패 결과:', failureResult);
    return failureResult;
  }
});

// 관리자 알림 전송 함수
async function sendAdminAlert(alertData: {
  type: string;
  title: string;
  message: string;
  impUid: string;
  userId: string;
}) {
  try {
    logger.info('관리자 알림 전송 시작:', alertData);
    
    // 관리자 알림을 Firestore에 저장
    await admin.firestore()
      .collection('admin_alerts')
      .add({
        ...alertData,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        status: 'unread',
        priority: alertData.type.includes('failed') ? 'high' : 'medium',
      });

    // TODO: 실제 관리자에게 푸시 알림, 이메일, 슬랙 등으로 알림 전송
    // 예: await sendSlackNotification(alertData);
    // 예: await sendAdminEmail(alertData);
    
    logger.info('관리자 알림 전송 완료');
  } catch (error) {
    logger.error('관리자 알림 전송 실패:', error);
  }
}
