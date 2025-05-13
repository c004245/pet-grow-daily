package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import javax.inject.Inject

class DefaultFirestoreAlbumDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
): FirestoreAlbumDataSource {

    override suspend fun saveAlbumRecord(record: AlbumRecord, userId: Long) {
        try {
            val recordMap = hashMapOf(
                "date" to record.date,
                "content" to record.content,
                "firstImage" to record.firstImage,
                "secondImage" to record.secondImage,
            )

            val userAlbumCollection = firestore.collection("users")
                .document(userId.toString())
                .collection("albums")

            userAlbumCollection.add(recordMap).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
