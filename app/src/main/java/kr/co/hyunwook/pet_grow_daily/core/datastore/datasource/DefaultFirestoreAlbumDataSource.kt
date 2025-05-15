package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import android.net.Uri
import java.io.File
import javax.inject.Inject

class DefaultFirestoreAlbumDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
): FirestoreAlbumDataSource {

    override suspend fun saveAlbumRecord(record: AlbumRecord, userId: Long) {
        try {

            val firstImageUrl = record.firstImage?.let { updateImageToStorage(it, userId) }
            val secondImageUrl = record.secondImage?.let { updateImageToStorage(it, userId) }

            val recordMap = hashMapOf(
                "date" to record.date,
                "content" to record.content,
                "firstImage" to firstImageUrl,
                "secondImage" to secondImageUrl,
            )

            val userAlbumCollection = firestore.collection("users")
                .document(userId.toString())
                .collection("albums")

            userAlbumCollection.add(recordMap).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private suspend fun updateImageToStorage(localPath: String, userId: Long): String? {
        return try {
            val file = File(localPath)
            if (!file.exists()) return null

            val fileName = file.name
            val storageRef = storage.reference
                .child("users")
                .child(userId.toString())
                .child("albums")
                .child(fileName)

            storageRef.putFile(Uri.fromFile(file)).await()

            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
