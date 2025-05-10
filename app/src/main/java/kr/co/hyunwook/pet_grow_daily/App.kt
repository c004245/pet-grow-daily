package kr.co.hyunwook.pet_grow_daily

import com.kakao.sdk.common.KakaoSdk
import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import android.content.pm.PackageManager
import android.util.Log
import java.security.MessageDigest
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@HiltAndroidApp
class App: Application() {

    @OptIn(ExperimentalEncodingApi::class)
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "42cb5a001e4aad8458c0b26f5e582da7")
    }
}