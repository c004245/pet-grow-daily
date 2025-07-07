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
import PDFDocument from "pdfkit";
import axios from "axios";

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

// PDF 생성 Function
export const generateAlbumPdf = onCall(async (request) => {
  try {
    const { orderId } = request.data;
    
    // 1. Firebase Auth 인증 확인
    if (!request.auth) {
      throw new Error('로그인이 필요합니다.');
    }

    // 2. 인증된 사용자 ID 가져오기
    const userId = request.auth.uid;
    logger.info(`PDF 생성 시작 - OrderId: ${orderId}, UserId: ${userId}`);

    // 3. 해당 사용자의 주문인지 확인
    const orderDoc = await admin.firestore()
      .collection('users').doc(userId)
      .collection('orders').doc(orderId)
      .get();

    if (!orderDoc.exists) {
      throw new Error('주문을 찾을 수 없습니다.');
    }

    const orderData = orderDoc.data()!;
    const { selectedImages } = orderData;

    logger.info(`선택된 이미지 개수: ${selectedImages?.length || 0}`);

    // 4. PDF 생성 (이미지만)
    const pdfBuffer = await createAlbumPdf(selectedImages);

    // 5. Storage에 PDF 업로드 (orders 폴더에)
    const bucket = admin.storage().bucket();
    const pdfFileName = `${orderId}_pdf.pdf`;
    const fileName = `orders/${pdfFileName}`;
    const file = bucket.file(fileName);

    await file.save(pdfBuffer, {
      metadata: {
        contentType: 'application/pdf',
      },
    });

    // 6. 공개 URL 생성
    await file.makePublic();
    const pdfUrl = `https://storage.googleapis.com/${bucket.name}/${fileName}`;

    // 7. Firestore에 PDF URL 업데이트
    await orderDoc.ref.update({
      pdfUrl: pdfUrl,
      pdfGeneratedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    logger.info(`PDF 생성 완료 - URL: ${pdfUrl}`);

    return {
      success: true,
      pdfUrl: pdfUrl,
      message: 'PDF 생성이 완료되었습니다.'
    };

  } catch (error) {
    logger.error('PDF 생성 실패:', error);
    throw new Error('PDF 생성에 실패했습니다.');
  }
});

// PDF 생성 함수 (이미지만)
async function createAlbumPdf(
  imageUrls: string[], 
): Promise<Buffer> {
  return new Promise(async (resolve, reject) => {
    try {
      const doc = new PDFDocument({ 
        size: 'A4', 
        margin: 0
      });
      
      const chunks: Buffer[] = [];

      doc.on('data', (chunk) => chunks.push(chunk));
      doc.on('end', () => resolve(Buffer.concat(chunks)));
      doc.on('error', (error) => reject(error));

      // 이미지가 없는 경우
      if (!imageUrls || imageUrls.length === 0) {
        doc.end();
        return;
      }

      // 각 이미지를 한 페이지씩 배치
      const pageWidth = doc.page.width;
      const pageHeight = doc.page.height;
      const maxImages = Math.min(imageUrls.length, 4); // 최대 4개 이미지

      for (let i = 0; i < maxImages; i++) {
        const imageUrl = imageUrls[i];
        
        if (imageUrl) {
          // 첫 번째 이미지가 아니면 새 페이지 추가
          if (i > 0) {
            doc.addPage();
          }

          try {
            logger.info(`이미지 다운로드 시작: ${imageUrl}`);
            
            const response = await axios.get(imageUrl, { 
              responseType: 'arraybuffer',
              timeout: 15000,
              headers: {
                'User-Agent': 'Pet-Grow-Daily-PDF-Generator'
              }
            });
            
            const imageBuffer = Buffer.from(response.data);

            // 이미지를 전체 페이지에 맞춰 배치
            doc.image(imageBuffer, 0, 0, { 
              width: pageWidth, 
              height: pageHeight,
              fit: [pageWidth, pageHeight],
              align: 'center',
              valign: 'center'
            });
            
            logger.info(`이미지 ${i + 1} 추가 완료`);
            
          } catch (imageError) {
            logger.error(`이미지 로드 실패: ${imageUrl}`, imageError);
          }
        }
      }

      doc.end();

    } catch (error) {
      logger.error('PDF 생성 중 오류:', error);
      reject(error);
    }
  });
}
