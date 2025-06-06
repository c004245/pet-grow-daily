package kr.co.hyunwook.pet_grow_daily.core.data.di

import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.AlbumPreferencesDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.DefaultAlbumPreferencesDataSource
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.DefaultFirestoreAlbumDataSource
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.FirestoreAlbumDataSource

@InstallIn(SingletonComponent::class)
@Module
internal abstract class DataStoreModule {

    @Binds
    abstract fun bindAlbumLocalDataSource(
        dataSource: DefaultAlbumPreferencesDataSource,
    ): AlbumPreferencesDataSource

    @Binds
    abstract fun bindAlbumRemoteDataSource(
        dataSource: DefaultFirestoreAlbumDataSource
    ): FirestoreAlbumDataSource
}