plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.driverattentiveness"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.driverattentiveness"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildTypes {
            release {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                buildConfigField ("String", "API_KEY", "\"8a0d3f9a88b34b619fa80bf987ab913b\"")
            }
            debug {
                buildConfigField ("String", "API_KEY", "\"8a0d3f9a88b34b619fa80bf987ab913b\"")
            }

        }

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
    buildFeatures {
        viewBinding = true
        mlModelBinding = true
        buildConfig = true
    }


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation (libs.androidx.fragment.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.gridlayout)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //preference
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.ktx)

    implementation (libs.androidx.fragment.ktx.v161)
    implementation (libs.androidx.navigation.fragment.ktx.v282)
    implementation (libs.androidx.navigation.ui.ktx.v282)
    val cameraxVersion = "1.4.0-alpha03"
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")

    implementation("org.tensorflow:tensorflow-lite:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

    //retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    //circle image
    implementation (libs.circleimageview)

    //fused location
    implementation(libs.play.services.maps.v1820)
    implementation(libs.play.services.location)

//    implementation (libs.socket.io.client)
//    implementation (libs.okhttp)
//    implementation (libs.socket.io.client)

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.15.1")

    implementation ("io.socket:socket.io-client:2.1.1")

    implementation("androidx.core:core-splashscreen:1.0.0")
}