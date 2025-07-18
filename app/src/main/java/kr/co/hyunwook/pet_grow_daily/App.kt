package kr.co.hyunwook.pet_grow_daily

import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.kakao.sdk.common.KakaoSdk
import android.app.Application
import android.graphics.Bitmap.Config
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App: Application(), Configuration.Provider {

    companion object {
        lateinit var db: FirebaseFirestore
            private set
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
    }


    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        KakaoSdk.init(this, "42cb5a001e4aad8458c0b26f5e582da7")

        db = Firebase.firestore
    }
}