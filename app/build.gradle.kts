import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

buildscript {
    dependencies{
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.10.2")
    }
}

plugins{
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("org.jetbrains.dokka") version "1.4.10"
}

android {
    signingConfigs {
        create("release") {
            enableV3Signing = true
            enableV4Signing = true
            storeFile = file(gradleLocalProperties(project.rootDir).getProperty("storeFile"))
            storePassword = gradleLocalProperties(project.rootDir).getProperty("storePassword")
            keyAlias = gradleLocalProperties(project.rootDir).getProperty("keyAlias")
            keyPassword = gradleLocalProperties(project.rootDir).getProperty("keyPassword")
        }
    }
    compileSdkVersion(30)
    buildToolsVersion = "30.0.2"

    defaultConfig {
        applicationId = "xyz.santtu.materialcarrot"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 3
        versionName = "1.0.1"

        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }
    buildFeatures{
        viewBinding = true
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

    implementation(project(mapOf("path" to ":materialcarrotrepository")))
    implementation(project(mapOf("path" to ":materialcarrotutils")))

    implementation("androidx.core:core:1.5.0-alpha04")
    implementation("androidx.core:core-ktx:1.5.0-alpha04")

    implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")

    implementation("androidx.appcompat:appcompat:1.3.0-alpha02")
    implementation("androidx.appcompat:appcompat-resources:1.3.0-alpha02")

    implementation("androidx.collection:collection-ktx:1.1.0")

    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha02")

    implementation("androidx.fragment:fragment-ktx:1.3.0-beta01")

    implementation("androidx.room:room-runtime:2.3.0-alpha03")
    implementation("androidx.room:room-ktx:2.3.0-alpha03")
    kapt("androidx.room:room-compiler:2.3.0-alpha03")

    implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:2.3.0-beta01")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.0-beta01")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0-beta01")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.3.0-beta01")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.0-beta01")

    implementation("androidx.navigation:navigation-ui-ktx:2.3.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.1")

    implementation("androidx.constraintlayout:constraintlayout:2.0.3")

    implementation("com.google.android.material:material:1.3.0-alpha03")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.0")

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

    androidTestImplementation("androidx.room:room-testing:2.3.0-alpha03")
}

tasks.dokkaHtml.configure {
    dokkaSourceSets {
        named("main") {
            noAndroidSdkLink.set(false)
        }
    }
}