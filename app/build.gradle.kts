plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("com.google.cloud.tools.endpoints-framework-client")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "tw.supra.suclear"
        minSdkVersion(23)
        targetSdkVersion(28)
        versionCode = 2
        versionName = "1.0.1-alpha"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            setMinifyEnabled(false)
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        setTargetCompatibility(1.8)
        setSourceCompatibility(1.8)
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.arch.core:core-common:2.0.0")
//    implementation("androidx.arch.core:core:2.0.0-rc01")
    implementation("androidx.core:core:1.1.0-alpha05")
    implementation("androidx.core:core-ktx:1.1.0-alpha05")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.21")
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("com.google.android.material:material:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.0.0")
    implementation("androidx.annotation:annotation:1.0.2")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.lifecycle:lifecycle-extensions:2.1.0-alpha03")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.0.0")
    implementation("android.arch.navigation:navigation-fragment-ktx:1.0.0")
    implementation("android.arch.navigation:navigation-ui-ktx:1.0.0")
    implementation("androidx.vectordrawable:vectordrawable:1.0.1")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.mediarouter:mediarouter:1.0.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.palette:palette:1.0.0")
    implementation("androidx.leanback:leanback:1.0.0")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.2")
    implementation("tw.supra.lib:supower:0.1.9")
    implementation("com.google.code.gson:gson:2.8.2")
    implementation("com.squareup.okhttp3:okhttp:3.12.1")
    implementation("com.squareup.retrofit2:retrofit:2.3.0")
    implementation("com.github.bumptech.glide:glide:4.0.0-RC1")
    implementation("com.koushikdutta.ion:ion:2.2.1")
    implementation("net.grandcentrix.tray:tray:0.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.0.0-RC1")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0-alpha02")
    implementation("com.mikepenz:materialdrawer:6.1.1")
    implementation("com.google.firebase:firebase-core:16.0.8")


    // V2: Endpoints Framework v2 migration
//    endpointsServer(project(":backend", "endpoints"))
    implementation("com.google.api-client:google-api-client:")
    implementation("com.google.http-client:google-http-client-android:1.28.0")
//    implementation("com.google.appengine:appengine-api-1.0-sdk:+")

    implementation("com.google.api-client:google-api-client-android:+"){
        exclude(module = "httpclient") // for android projects
    }
}

//endpointsClient {
//    discoveryDocs = ['src/endpoints/myApi-v1-rest.discovery']
//}
