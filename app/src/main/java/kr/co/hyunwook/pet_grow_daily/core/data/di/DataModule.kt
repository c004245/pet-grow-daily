package kr.co.hyunwook.pet_grow_daily.core.data.di

import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    fun bindsAlbumRepository(albumRepositoryImpl: AlbumRepositoryImpl): AlbumRepository
}