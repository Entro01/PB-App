// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication).apply(false)
    alias(libs.plugins.jetbrainsKotlinAndroid).apply(false)
    kotlin("jvm").version(libs.versions.serialization).apply(true)
    kotlin("plugin.serialization").version(libs.versions.serialization).apply(true)
}