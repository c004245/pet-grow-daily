package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AnotherPetModel
import android.net.Uri
import android.util.Log
import java.io.File
import javax.inject.Inject
import androidx.compose.animation.core.snap

class DefaultFirestoreAlbumDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : FirestoreAlbumDataSource {

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

                if (firstImage.isNotEmpty()) {
                    models.add(
                        AnotherPetModel(
                            imageUrl = firstImage,
                            content = content
                        )
                    )
                }
                if (secondImage.isNotEmpty()) {
                    models.add(
                        AnotherPetModel(
                            imageUrl = secondImage,
                            content = content
                        )
                    )
                }
                models
            }

            emit(result)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }



}
