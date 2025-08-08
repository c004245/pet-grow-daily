package kr.co.hyunwook.pet_grow_daily.analytics

import android.app.Application

interface Analytics {

    fun init(app: Application)

    fun setIdentify()

    fun track(event: String)

    fun track(event: String, properties: Map<String, Any>?)
}