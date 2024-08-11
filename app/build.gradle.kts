plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mqtttest"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mqtttest"
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
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
   // implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    implementation ("com.google.android.gms:play-services-nearby:18.0.0")

    implementation ("com.github.hannesa2:paho.mqtt.android:3.3.5")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation ("com.jakewharton.threetenabp:threetenabp:1.4.4")

}