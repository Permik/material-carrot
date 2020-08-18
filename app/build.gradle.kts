import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins{
    id("com.android.application")
    id("androidx.navigation.safeargs")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("org.jetbrains.dokka") version "1.4.0-rc"
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.2"

    defaultConfig {
        applicationId = "xyz.santtu.materialcarrot"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    packagingOptions {
        exclude("META-INF/atomicfu.kotlin_module")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))

    implementation("androidx.appcompat:appcompat:1.3.0-alpha01")
    implementation("androidx.appcompat:appcompat-resources:1.3.0-alpha01")

    implementation("androidx.core:core-ktx:1.3.1")

    implementation ("androidx.collection:collection-ktx:1.1.0")

    implementation("androidx.fragment:fragment-ktx:1.3.0-alpha07")

    implementation("androidx.room:room-runtime:2.3.0-alpha02")
    implementation("androidx.room:room-ktx:2.3.0-alpha02")
    kapt("androidx.room:room-compiler:2.3.0-alpha02")

    implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:2.3.0-alpha06")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.0-alpha06")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0-alpha06")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.3.0-alpha06")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.0-alpha06")

    implementation("androidx.navigation:navigation-ui-ktx:2.3.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.0")

    implementation("androidx.constraintlayout:constraintlayout:1.1.3")

    implementation("com.google.android.material:material:1.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.8")

    testImplementation("junit:junit:4.13")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.10")
    androidTestImplementation("androidx.room:room-testing:2.3.0-alpha02")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}

tasks.dokkaHtml {
    outputDirectory = "$buildDir/dokka"
}