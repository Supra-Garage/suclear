plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "tw.supra.suclear"
        minSdkVersion(28)
        targetSdkVersion(29)
        versionCode = 2
        versionName = "1.0.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }

        getByName("release") {
            signingConfig = signingConfigs.getByName("debug")
//            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
            isZipAlignEnabled = true
            isMinifyEnabled = true
            proguardFile(getDefaultProguardFile("proguard-android.txt"))
            // global proguard settings
            proguardFile(file("proguard-rules.pro"))
            // library proguard settings
//            val files = rootProject.file("proguard")
//                    .listFiles()
//                    .filter { it.name.startsWith("proguard") }
//                    .toTypedArray()
//            proguardFiles(*files)
        }
    }

//    signingConfigs {
//        create("release") {
//            storeFile = rootProject.file("release.keystore")
//            storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
//            keyAlias = System.getenv("ANDROID_KEYSTORE_ALIAS")
//            keyPassword = System.getenv("ANDROID_KEYSTORE_PRIVATE_KEY_PASSWORD")
//        }
//    }

    compileOptions {
        setTargetCompatibility(1.8)
        setSourceCompatibility(1.8)
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.arch.core:core-common:2.1.0")
//    implementation("androidx.arch.core:core:2.0.0-rc01")
    implementation("androidx.core:core:1.3.0-alpha02")
    implementation("androidx.core:core-ktx:1.3.0-alpha02")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.70")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.annotation:annotation:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("android.arch.navigation:navigation-fragment-ktx:1.0.0")
    implementation("android.arch.navigation:navigation-ui-ktx:1.0.0")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.mediarouter:mediarouter:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.palette:palette:1.0.0")
    implementation("androidx.leanback:leanback:1.0.0")
    testImplementation("junit:junit:4.12")
    implementation("tw.supra.lib:supower:0.1.9")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("com.koushikdutta.ion:ion:2.2.1")
    implementation("net.grandcentrix.tray:tray:0.12.0")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    implementation("com.mikepenz:materialdrawer:6.1.1")

    implementation("com.squareup.okhttp3:okhttp:4.4.0")
    implementation("com.squareup.retrofit2:retrofit:2.8.0")
    implementation("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")

    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    // Because RxAndroid releases are few and far between, it is recommended you also
    // explicitly depend on RxJava's latest version for bug fixes and new features.
    // (see https://github.com/ReactiveX/RxJava/releases for latest 3.x.x version)
    implementation("io.reactivex.rxjava3:rxjava:3.0.0")
}
