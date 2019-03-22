// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.3.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.21")
        classpath("android.arch.navigation:navigation-safe-args-gradle-plugin:1.0.0")
        classpath("com.google.gms:google-services:4.2.0")
        // V2: Include this dependency when using Endpoints Framework v2
        classpath("com.google.guava:guava:24.1-jre")
//        classpath("com.google.cloud.tools:endpoints-framework-gradle-plugin:1.0.2")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files

        // App Engine Gradle plugin
        classpath("com.google.cloud.tools:appengine-gradle-plugin:1.3.4")
        // Endpoints Frameworks Gradle plugin
        classpath("com.google.cloud.tools:endpoints-framework-gradle-plugin:2.0.1")

    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

//task clean (type: Delete) {
//    delete rootProject . buildDir
//}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

//ext.deps = [:]
//def versions =[:]
//versions.lifecycle = "1.1.1"
//versions.support = "28.1.1"
//versions.kotlin = kotlin_version
//versions.navigation = "1.0.0-alpha01"
//versions.min_sdk = 23
////versions.target_sdk = 28
//versions.constraint_layout = "1.0.2"
//ext.versions = versions
