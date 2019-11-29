// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.50")
        classpath("android.arch.navigation:navigation-safe-args-gradle-plugin:1.0.0")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

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
