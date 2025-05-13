package kr.co.hyunwook.pet_grow_daily.core.data.di

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import kr.co.hyunwook.pet_grow_daily.core.database.AppDatabase
import kr.co.hyunwook.pet_grow_daily.core.database.AlbumRecordDao
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

    private const val ALBUM_DATASTORE_NAME = "ALBUM_PREFERENCE"

    private val Context.albumDataSource by preferencesDataStore(ALBUM_DATASTORE_NAME)

    @Provides
    @Singleton
    @Named("album")
    fun provideAlbumDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> =
        context.albumDataSource

    @Provides
    @Singleton
    fun provideAppDatabase(
        application: Application,
    ): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "PetAlbum.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideAlbumRecordDao(appDatabase: AppDatabase): AlbumRecordDao {
        return appDatabase.albumRecordDao()
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }
}