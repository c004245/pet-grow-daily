package kr.co.hyunwook.pet_grow_daily.core.data.di

import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.hyunwook.pet_grow_daily.core.data.repository.delivery.DeliveryRepository
import kr.co.hyunwook.pet_grow_daily.core.data.repository.delivery.DeliveryRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    fun bindsAlbumRepository(albumRepositoryImpl: AlbumRepositoryImpl): AlbumRepository

    @Binds
    fun bindsDeliveryRepository(deliveryRepositoryImpl: DeliveryRepositoryImpl): DeliveryRepository
}