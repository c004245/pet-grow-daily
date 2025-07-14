package kr.co.hyunwook.pet_grow_daily.core.config

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.OrderProductListModel

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigWrapper @Inject constructor() {

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance().apply {
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build()

            setConfigSettingsAsync(configSettings)
            setDefaultsAsync(R.xml.remote_config_defaults)
        }
    }

    fun fetchOrderProductList(onComplete:  (OrderProductListModel?) -> Unit) {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val orderProductListJson =  remoteConfig.getString("order_product_list")
            try {
                val response = Gson().fromJson(
                    orderProductListJson,
                    OrderProductListModel::class.java
                )
                onComplete(response)
            } catch (e: Exception) {
                onComplete(null)
            }

        } else {
            onComplete(null)
        }
        }

    }
}