import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlin.serialization)
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    alias(libs.plugins.google.services)
    id("com.google.firebase.crashlytics") version "2.9.9"

}

// Load keystore properties
val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("app/key.properties")
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

// Load local.properties for secrets (e.g., SLACK_WEBHOOK)
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {

    namespace = "kr.co.hyunwook.pet_grow_daily"
    compileSdk = 35

    defaultConfig {
        applicationId = "kr.co.hyunwook.pet_grow_daily"
        minSdk = 24
        targetSdk = 35
        versionCode = 12
        versionName = "1.0.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties.getProperty("storeFile") ?: "key.jks")
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
        }
    }

    val slackWebhook = localProperties.getProperty("SLACK_WEBHOOK") as String
    val mixpanelToken = localProperties.getProperty("MIXPANEL_TOKEN") ?: ""

    buildTypes {
        debug {
            buildConfigField("boolean", "DEBUG", "true")
            buildConfigField("String", "SLACK_WEBHOOK", "\"$slackWebhook\"")
            buildConfigField("String", "MIXPANEL_TOKEN", "\"$mixpanelToken\"")

        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "DEBUG", "false")
            buildConfigField("String", "SLACK_WEBHOOK", "\"$slackWebhook\"")
            buildConfigField("String", "MIXPANEL_TOKEN", "\"$mixpanelToken\"")

            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "win32-x86/attach_hotspot_windows.dll"
            excludes += "win32-x86-64/attach_hotspot_windows.dll"
            excludes += "**/attach_hotspot_windows.dll"
            excludes += "META-INF/licenses/ASM"
            excludes += "META-INF/licenses/*"
            excludes += "META-INF/LICENSE*"
            excludes += "META-INF/NOTICE*"
        }
    }

}



dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.functions.ktx)
    implementation(libs.hilt.work)

    androidTestImplementation(platform(libs.androidx.compose.bom))

    testImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.compose.navigation)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.core)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.work)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)

    kapt(libs.androidx.room.compiler)

    implementation(libs.glide.compose)
    implementation(libs.androidx.datastore)

    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.functions.ktx)
    implementation(libs.firebase.appcheck)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.analytics.ktx)

    implementation("com.google.firebase:firebase-crashlytics-ktx")

    testImplementation(libs.kotest.engine)
    testImplementation(libs.kotest.assertions)

    androidTestImplementation(libs.kotest.runner.junit5.jvm)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.androidx.ui.test.junit4)

    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicator)

    implementation(libs.kakao.login)
    implementation(libs.firestore.ktx)
    implementation(libs.firebase.storage.ktx)

    // 아임포트 SDK
    implementation("com.github.iamport:iamport-android:1.4.8")

    implementation(libs.lottie.compose)

    // OkHttp for HTTP requests
    implementation(libs.okhttp)

    implementation(libs.work.runtime.ktx)

    implementation(libs.mixpanel)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
