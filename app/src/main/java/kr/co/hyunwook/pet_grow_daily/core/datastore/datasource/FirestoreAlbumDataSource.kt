package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord

interface FirestoreAlbumDataSource {
    suspend fun saveAlbumRecord(record: AlbumRecord, userId: Long)
}
