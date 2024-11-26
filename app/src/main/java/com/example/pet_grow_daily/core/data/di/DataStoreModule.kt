package com.example.pet_grow_daily.core.data.di

import com.example.pet_grow_daily.core.datastore.datasource.DefaultGrowPreferencesDataSource
import com.example.pet_grow_daily.core.datastore.datasource.GrowPreferencesDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal abstract class DataStoreModule {

    @Binds
    abstract fun bindGrowLocalDataSource(
        dataSource: DefaultGrowPreferencesDataSource,
    ): GrowPreferencesDataSource

}