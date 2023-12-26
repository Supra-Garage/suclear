// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {

    /**
     * Use `apply false` in the top-level build.gradle file to add a Gradle
     * plugin as a build dependency but not apply it to the current (root)
     * project. Don't use `apply false` in sub-projects. For more information,
     * see Applying external plugins with same version to subprojects.
     */

    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

//buildscript {
//    ext.kotlin_version = '1.8.20'
//    repositories {
//        google()
//        mavenCentral()
//    }
//    dependencies {
//        classpath 'com.android.tools.build:gradle:8.2.0'
//        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
//        classpath 'android.arch.navigation:navigation-safe-args-gradle-plugin:1.0.0'
//
//        // NOTE: Do not place your application dependencies here; they belong
//        // in the individual module build.gradle.bak files
//    }
//}
//
//plugins {
//    id 'com.android.application' version '8.2.0' apply false
//    id 'com.android.library' version '8.2.0' apply false
//    id 'org.jetbrains.kotlin.android' version '1.9.20' apply false
//}
//
//allprojects {
//    repositories {
//        google()
//        mavenCentral()
//        maven { url 'https://oss.jfrog.org/libs-snapshot' }
//    }
//
//}
//
//task clean(type: Delete) {
//    delete rootProject.buildDir
//}

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
