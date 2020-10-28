import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

buildscript {
    dependencies{
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.10.2")
    }
}

plugins{
    id("com.android.library")
    kotlin("android")
    id("org.jetbrains.dokka") version "1.4.10"
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.2"

    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
        consumerProguardFiles("consumer-rules.pro")
    }
    signingConfigs {
        create("release") {
            enableV3Signing = true
            enableV4Signing = true
            storeFile = file(gradleLocalProperties(project.rootDir).getProperty("storeFile"))
            storePassword = gradleLocalProperties(project.rootDir).getProperty("storePassword")
            keyAlias = gradleLocalProperties(project.rootDir).getProperty("keyAlias")
            keyPassword = gradleLocalProperties(project.rootDir).getProperty("storePassword")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk8"))

    testImplementation("junit:junit:4.13.1")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.10")

    androidTestImplementation("androidx.test:core:1.3.0")
    androidTestImplementation ("androidx.test:runner:1.3.0")
    androidTestImplementation ("androidx.test:rules:1.3.0")

    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation ("androidx.test.ext:truth:1.3.0")
    androidTestImplementation ("com.google.truth:truth:1.1")
    androidTestImplementation("com.google.truth.extensions:truth-java8-extension:1.1")
}