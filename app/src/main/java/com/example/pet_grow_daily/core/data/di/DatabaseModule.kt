package com.example.pet_grow_daily.core.data.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.pet_grow_daily.core.database.AppDatabase
import com.example.pet_grow_daily.core.database.GrowRecordDao
import com.example.pet_grow_daily.core.datastore.datasource.DefaultGrowPreferencesDataSource
import com.example.pet_grow_daily.core.datastore.datasource.GrowPreferencesDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    private const val GROW_DATASTORE_NAME = "GROW_PREFERENCES"

    private val Context.growDataSource by preferencesDataStore(GROW_DATASTORE_NAME)

    @Provides
    @Singleton
    @Named("grow")
    fun provideGrowDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =
        context.growDataSource

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