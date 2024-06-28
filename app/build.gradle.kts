plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.project.fooddiaryv3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.fooddiaryv3"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("androidx.appcompat:appcompat:1.7.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.annotation:annotation:1.8.0")
    implementation ("com.google.android.gms:play-services-maps:18.0.2")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.firebase:firebase-auth:23.0.0")
    implementation ("com.google.firebase:firebase-database:21.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))

}