// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}

//tasks.register("clean", Delete::class) {
//    delete(rootProject.layout.buildDirectory)
//}

buildscript {
    extra.apply {
        set("room_version", "2.5.2")
    }
}
