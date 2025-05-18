package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import android.net.Uri
import android.util.Log
import java.io.File
import javax.inject.Inject
import androidx.compose.animation.core.snap

class DefaultFirestoreAlbumDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
): FirestoreAlbumDataSource {

    override suspend fun saveAlbumRecord(record: AlbumRecord, userId: Long) {
        try {

            Log.d("HWO", "saveAlbumRecord: ${record.firstImage} -- ${record.secondImage}")

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


}
