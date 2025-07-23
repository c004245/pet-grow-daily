package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AnotherPetModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile
import android.net.Uri
import android.util.Log
import java.io.File
import java.util.*
import javax.inject.Inject
import androidx.compose.animation.core.snap
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import java.text.SimpleDateFormat

class DefaultFirestoreAlbumDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) : FirestoreAlbumDataSource {

    override suspend fun savePetProfile(profile: PetProfile, userId: Long) {
        try {
            val profileMap = hashMapOf(
                "name" to profile.name,
                "profileImage" to profile.profileImageUrl
            )

            firestore.collection("users")
                .document(userId.toString())
                .collection("profile")
                .document("main") // 고정 문서 ID 사용
                .set(profileMap)
                .await()
        } catch (e: Exception) {
            Log.e("DefaultFirestoreAlbumDataSource", "프로필 저장 실패: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getTodayZipFileCount(): Int {
        return suspendCancellableCoroutine { continuation ->
            try {
                Log.d("HWO", "Firebase Storage에서 오늘 ZIP 파일 개수 조회 시작")

                val storageRef = Firebase.storage.reference.child("orders")

                storageRef.listAll()
                    .addOnSuccessListener { listResult ->
                        val today = Calendar.getInstance()
                        val todayYear = today.get(Calendar.YEAR)
                        val todayMonth = today.get(Calendar.MONTH) + 1
                        val todayDay = today.get(Calendar.DAY_OF_MONTH)

                        // ZIP 파일만 필터링
                        val zipFiles = listResult.items.filter { it.name.endsWith(".zip") }

                        if (zipFiles.isEmpty()) {
                            Log.d("HWO", "orders 폴더에 ZIP 파일이 없습니다.")
                            continuation.resume(0)
                            return@addOnSuccessListener
                        }

                        var processedCount = 0
                        var todayZipCount = 0

                        Log.d("HWO", "총 ${zipFiles.size}개의 ZIP 파일 발견")

                        zipFiles.forEach { fileRef ->
                            fileRef.metadata.addOnSuccessListener { metadata ->
                                val creationTime = metadata.creationTimeMillis
                                val fileDate = Calendar.getInstance().apply {
                                    timeInMillis = creationTime
                                }

                                val fileYear = fileDate.get(Calendar.YEAR)
                                val fileMonth = fileDate.get(Calendar.MONTH) + 1
                                val fileDay = fileDate.get(Calendar.DAY_OF_MONTH)

                                Log.d("HWO", "${fileRef.name}: $fileYear-$fileMonth-$fileDay")

                                // 오늘 생성된 파일인지 확인
                                if (fileYear == todayYear && fileMonth == todayMonth && fileDay == todayDay) {
                                    todayZipCount++
                                }

                                processedCount++

                                // 모든 파일을 처리했을 때 결과 반환
                                if (processedCount == zipFiles.size) {
                                    Log.d("HWO", "오늘 생성된 ZIP 파일 개수: $todayZipCount")
                                    continuation.resume(todayZipCount)
                                }
                            }.addOnFailureListener { exception ->
                                Log.e("HWO", "${fileRef.name} 메타데이터 조회 실패: ${exception.message}")
                                processedCount++

                                // 실패한 경우에도 카운트 처리
                                if (processedCount == zipFiles.size) {
                                    Log.d("HWO", "오늘 생성된 ZIP 파일 개수: $todayZipCount")
                                    continuation.resume(todayZipCount)
                                }
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("HWO", "Firebase Storage 조회 실패: ${exception.message}")
                        continuation.resume(0)
                    }

            } catch (e: Exception) {
                Log.e("HWO", "오늘 ZIP 파일 개수 조회 예외", e)
                continuation.resume(0)
            }
        }
    }

    override suspend fun hasPetProfile(userId: Long): Flow<Boolean> = flow {
         try {
            val snapshot = firestore.collection("users")
                .document(userId.toString())
                .collection("profile")
                .limit(1)
                .get()
                .await()

            emit(!snapshot.isEmpty)
        } catch (e: Exception) {
            Log.e("HWO", "Error checking pet profile: ${e.message}", e)
             emit(false)
        }
    }

    override suspend fun saveAlbumRecord(record: AlbumRecord, userId: Long) {
        try {

            Log.d("HWO", "saveAlbumRecord: ${record.firstImage} -- ${record.secondImage}")

            val recordMap = hashMapOf(
                "date" to record.date,
                "content" to record.content,
                "firstImage" to record.firstImage,
                "secondImage" to record.secondImage,
                "isPublic" to record.isPublic
            )

            val userAlbumCollection = firestore.collection("users")
                .document(userId.toString())
                .collection("albums")

            userAlbumCollection.add(recordMap).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun saveOrderRecord(
        selectedAlbumRecords: List<AlbumRecord>,
        deliveryInfo: DeliveryInfo,
        paymentInfo: Map<String, String>,
        userId: Long
    ): String {
        val orderId = "order_${System.currentTimeMillis()}"

        try {
            val selectedImageUrls = selectedAlbumRecords.flatMap { record ->
                listOfNotNull(
                    record.firstImage.takeIf { it.isNotEmpty() },
                    record.secondImage.takeIf { it.isNotEmpty() }
                )
            }

            Log.d("HWO", "주문 저장 시작 - OrderId: $orderId")
            Log.d("HWO", "선택된 이미지: ${selectedImageUrls.size}개")

            val orderMap = hashMapOf(
                "selectedImages" to selectedImageUrls,
                "deliveryInfo" to mapOf(
                    "zipCode" to deliveryInfo.zipCode,
                    "address" to deliveryInfo.address,
                    "detailAddress" to deliveryInfo.detailAddress,
                    "name" to deliveryInfo.name,
                    "phoneNumber" to deliveryInfo.phoneNumber
                ),
                "paymentInfo" to paymentInfo,
                "orderDate" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(userId.toString())
                .collection("orders")
                .document(orderId)
                .set(orderMap)
                .await()

            Log.d("HWO", "주문 저장 완료 - OrderId: $orderId")
            return orderId

        } catch (e: Exception) {
            Log.e("HWO", "주문 저장 실패: ${e.message}", e)
            throw e
        }
    }


    override suspend fun getUserAlbumCount(userId: Long): Int {
        return try {
            val userAlbumCollection = firestore.collection("users")
                .document(userId.toString())
                .collection("albums")

            val snapshot = userAlbumCollection.get().await()
            snapshot.size()
        } catch (e: Exception) {
            Log.d("HWO", "getUserAlbumCount: ${e.message}")
            0
        }
    }

    override suspend fun getTodayUserPhotoCount(): Int {
        return try {
            val calendar = Calendar.getInstance().apply {
                // 한국 표준시(Asia/Seoul)로 0시 세팅
                timeZone = TimeZone.getTimeZone("Asia/Seoul")
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val todayStart = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val tomorrowStart = calendar.timeInMillis
            // 전 유저의 albums에서 오늘 등록한 문서 모두 조회
            val albumsQuery = firestore.collectionGroup("albums")
                .whereGreaterThanOrEqualTo("date", todayStart)
                .whereLessThan("date", tomorrowStart)

            val snapshot = albumsQuery.get().await()

            // 경로에서 userId 추출: /users/{userId}/albums/{albumId}
            val userIdSet = HashSet<String>()
            snapshot.documents.forEach { doc ->
                val pathParts = doc.reference.path.split("/")
                if (pathParts.size >= 2) {
                    val userId = pathParts[1]
                    userIdSet.add(userId)
                }
            }
            Log.d("HWO", "오늘 사진 업로드한 유저 수: ${userIdSet.size}명 (중복제거)")
            userIdSet.size
        } catch (e: Exception) {
            Log.e("HWO", "getTodayRegisteredUserCount 실패: ${e.message}", e)
            0
        }
    }

    override suspend fun getAnotherPetAlbums(): Flow<List<AnotherPetModel>> = flow {
        try {
            val publicAlbumsQuery = firestore.collectionGroup("albums")
                .whereEqualTo("isPublic", true)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(30)

            val snapshot = publicAlbumsQuery.get().await()
            val result = snapshot.documents.flatMap { document ->
                val data = document.data ?: return@flatMap emptyList()

                val firstImage = data["firstImage"] as? String ?: ""
                val secondImage = data["secondImage"] as? String ?: ""
                val content = data["content"] as? String ?: ""

                val models = mutableListOf<AnotherPetModel>()

                models.add(
                    AnotherPetModel(
                    firstImage = firstImage,
                    secondImage = secondImage,
                    content = content
                    )
                )
                models
            }

            emit(result)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
