package kr.co.hyunwook.pet_grow_daily.core.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kr.co.hyunwook.pet_grow_daily.analytics.Analytics
import kr.co.hyunwook.pet_grow_daily.analytics.MixpanelAnalytics
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AnalyticsModule {
    @Provides
    @Singleton
    fun provideMixpanel(): Analytics {
        return MixpanelAnalytics()
    }
}