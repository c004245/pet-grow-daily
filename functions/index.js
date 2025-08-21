// index.js (CommonJS)

// ─────────────────────────────────────────────────────────────────────────────
// Imports
// ─────────────────────────────────────────────────────────────────────────────
const functions = require('firebase-functions');          // 기존 함수들이 사용 중일 수 있음
const functionsV1 = require('firebase-functions/v1');     // 새 랜덤 이미지 함수 전용 (v1 고정)
const admin = require('firebase-admin');
const { FieldPath } = require('firebase-admin/firestore');

// ─────────────────────────────────────────────────────────────────────────────
admin.initializeApp();

// ─────────────────────────────────────────────────────────────────────────────
// 기존에 사용 중인 함수(들)
// ─────────────────────────────────────────────────────────────────────────────
// 예) exports.someExistingFunction = functions.https.onRequest(async (req, res) => { ... });
// ※ 실제 프로젝트의 기존 함수들은 여기 그대로 두세요.


// ─────────────────────────────────────────────────────────────────────────────
// 유틸: 20자리 Firestore auto-id 스타일 랜덤 문자열
// ─────────────────────────────────────────────────────────────────────────────
function generateRandomDocId() {
  const CHARS = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < 20; i++) {
    result += CHARS.charAt(Math.floor(Math.random() * CHARS.length));
  }
  return result;
}

// ─────────────────────────────────────────────────────────────────────────────
// 새 함수: 공개 앨범 중에서 랜덤 이미지 1개 반환
// - 컬렉션 그룹 쿼리: users/*/albums
// - 조건: isPublic == true
// - 랜덤: documentId를 기준으로 startAt(randomId) → 없으면 역순 재시도
// ─────────────────────────────────────────────────────────────────────────────
exports.getPublicAnotherPetImage = functionsV1.https.onRequest(async (req, res) => {
  // CORS 헤더
  res.set('Access-Control-Allow-Origin', '*');
  res.set('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.set('Access-Control-Allow-Headers', 'Content-Type');

  // Preflight
  if (req.method === 'OPTIONS') {
    return res.status(204).send('');
  }

  try {
    const db = admin.firestore();
    const randomId = generateRandomDocId();

    console.log(`[getPublicAnotherPetImage] randomId: ${randomId}`);

    // 1차 시도: __name__ >= randomId
    let snap = await db
      .collectionGroup('albums')
      .where('isPublic', '==', true)
      .orderBy(FieldPath.documentId())
      .startAt(randomId)
      .limit(1)
      .get();

    // 없으면 역순으로 재시도
    if (snap.empty) {
      console.log('[getPublicAnotherPetImage] forward query empty, try reverse');
      snap = await db
        .collectionGroup('albums')
        .where('isPublic', '==', true)
        .orderBy(FieldPath.documentId(), 'desc')
        .startAt(randomId)
        .limit(1)
        .get();
    }

    if (snap.empty) {
      console.log('[getPublicAnotherPetImage] no public albums found');
      return res.status(404).json({
        error: 'No public images available',
        message: '공개된 사진이 없습니다.'
      });
    }

    const doc = snap.docs[0];
    const data = doc.data();

    const candidates = [];
    if (data.firstImage) candidates.push(data.firstImage);
    if (data.secondImage) candidates.push(data.secondImage);

    if (candidates.length === 0) {
      console.log('[getPublicAnotherPetImage] selected album has no image fields');
      return res.status(404).json({
        error: 'No images in selected album',
        message: '선택된 앨범에 이미지가 없습니다.'
      });
    }

    const imageUrl = candidates[Math.floor(Math.random() * candidates.length)];

    const response = {
      success: true,
      data: {
        imageUrl,
        content: data.content || '',
        date: data.date || null,
        documentId: doc.id
      },
      timestamp: new Date().toISOString()
    };

    console.log('[getPublicAnotherPetImage] success');
    return res.status(200).json(response);
  } catch (error) {
    console.error('[getPublicAnotherPetImage] error:', error);
    return res.status(500).json({
      error: 'Internal server error',
      message: '서버 내부 오류가 발생했습니다.',
      timestamp: new Date().toISOString()
    });
  }
});
