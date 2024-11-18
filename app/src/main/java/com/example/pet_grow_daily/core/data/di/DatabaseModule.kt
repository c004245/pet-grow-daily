package com.example.pet_grow_daily.core.data.di

import android.app.Application
import androidx.room.Room
import com.example.pet_grow_daily.core.database.AppDatabase
import com.example.pet_grow_daily.core.database.GrowRecordDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        application: Application,
    ): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "PetGrow.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideGrowRecordDao(appDatabase: AppDatabase): GrowRecordDao {
        return appDatabase.growRecordDao()
    }
}