/**
 * Import function triggers from their respective submodules:
 *
 * import {onCall} from "firebase-functions/v2/https";
 * import {onDocumentWritten} from "firebase-functions/v2/firestore";
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

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

