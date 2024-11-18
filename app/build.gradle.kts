plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.bardaval.musicapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bardaval.musicapp"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)




    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("androidx.media:media:1.7.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("io.coil-kt:coil:2.4.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation ("androidx.core:core-ktx:1.9.0")


    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0")
    implementation ("androidx.appcompat:appcompat:1.6.0")
    implementation ("androidx.recyclerview:recyclerview:1.2.1")


    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.0")
    implementation ("androidx.compose.ui:ui:1.3.0")
    implementation ("androidx.compose.material3:material3:1.0.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.3.0")
    implementation ("androidx.navigation:navigation-compose:2.6.0")




}