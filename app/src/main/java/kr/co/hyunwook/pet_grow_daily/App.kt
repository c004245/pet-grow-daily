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
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.auth
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

        // Firebase Auth 익명 로그인
        Firebase.auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("HWO", "Firebase Auth 익명 로그인 성공")
                } else {
                    Log.e("HWO", "Firebase Auth 익명 로그인 실패: ${task.exception?.message}")
                }
            }

        // Debug 빌드에서는 Debug Provider 로 App Check 토큰을 자동 발급합니다.
        // TODO: Firebase App Check API 활성화 후 주석 해제
        if (BuildConfig.DEBUG) {
            Firebase.appCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        }

        KakaoSdk.init(this, "42cb5a001e4aad8458c0b26f5e582da7")

        db = Firebase.firestore
    }
}