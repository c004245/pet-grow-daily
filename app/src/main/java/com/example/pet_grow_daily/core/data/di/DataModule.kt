package com.example.pet_grow_daily.core.data.di

import com.example.pet_grow_daily.core.data.repository.grow.GrowRepository
import com.example.pet_grow_daily.core.data.repository.grow.GrowRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {

    @Binds
    fun bindsGrowRepository(growRepositoryImpl: GrowRepositoryImpl): GrowRepository
}