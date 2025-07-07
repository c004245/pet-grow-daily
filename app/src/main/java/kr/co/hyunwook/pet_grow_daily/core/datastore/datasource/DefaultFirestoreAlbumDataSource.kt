package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AnotherPetModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile
import android.net.Uri
import android.util.Log
import java.io.File
import javax.inject.Inject
import androidx.compose.animation.core.snap
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo

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
