
plugins{
    id("com.android.library")
    kotlin("android")
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

    buildTypes {
        getByName("release") {
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

    testImplementation("junit:junit:4.13")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.10")

    androidTestImplementation("androidx.test:core:1.3.0")
    androidTestImplementation ("androidx.test:runner:1.3.0")
    androidTestImplementation ("androidx.test:rules:1.3.0")

    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation ("androidx.test.ext:truth:1.3.0")
    androidTestImplementation ("com.google.truth:truth:1.0.1")
    androidTestImplementation("com.google.truth.extensions:truth-java8-extension:1.0.1")
}