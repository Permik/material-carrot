
plugins{
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("org.jetbrains.dokka") version "1.4.0-rc"
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.2"

    defaultConfig {
        applicationId = "xyz.santtu.materialcarrotwear"
        minSdkVersion(28)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(mapOf("path" to ":materialcarrotrepository")))
    implementation(project(mapOf("path" to ":materialcarrotutils")))

    implementation("androidx.core:core:1.5.0-alpha04")
    implementation("androidx.core:core-ktx:1.5.0-alpha02")

    implementation("androidx.appcompat:appcompat:1.3.0-alpha02")
    implementation("androidx.appcompat:appcompat-resources:1.3.0-alpha02")

    implementation("androidx.activity:activity:1.2.0-alpha08")
    implementation("androidx.activity:activity-ktx:1.2.0-beta01")

    implementation("androidx.fragment:fragment:1.3.0-alpha08")
    implementation("androidx.fragment:fragment-ktx:1.3.0-alpha08")

    implementation("com.google.android.support:wearable:2.7.0")

    implementation("androidx.wear:wear:1.0.0")
    implementation("com.google.android.material:material:1.2.1")
    
    testImplementation("junit:junit:4.13")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.0.10")

    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")

    compileOnly("com.google.android.wearable:wearable:2.7.0")
}

tasks.dokkaHtml {
    outputDirectory = "$buildDir/dokka"
}