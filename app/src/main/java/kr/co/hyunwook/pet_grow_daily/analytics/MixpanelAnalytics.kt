package kr.co.hyunwook.pet_grow_daily.analytics

import android.app.Application
import com.mixpanel.android.mpmetrics.MixpanelAPI
import kr.co.hyunwook.pet_grow_daily.BuildConfig
import org.json.JSONObject

class MixpanelAnalytics(
): Analytics {


    lateinit var mp: MixpanelAPI

    override fun init(app: Application) {
        mp = MixpanelAPI.getInstance(app, BuildConfig.MIXPANEL_TOKEN, true)
    }

    override fun setIdentify() {
        // Mixpanel에서 사용자 식별 설정
    }

    override fun track(event: String) {
        mp.track(event)
    }

    override fun track(event: String, properties: Map<String, Any>?) {
        if (properties?.isEmpty() == true) {
            mp.track(event)
        } else {
            val props = JSONObject().apply {
                properties?.map { (k, v) ->
                    when (v) {
                        is Boolean -> put(k, v)
                        is Double -> put(k, v)
                        is Int -> put(k, v)
                        is Long -> put(k, v)
                        else -> put(k, v)
                    }
                }
            }
            mp.track(event, props)
        }
    }
}