import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins{
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        applicationId = "xyz.santtu.materialcarrotwear"
        minSdkVersion(28)
        targetSdkVersion(29)
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
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation("androidx.core:core-ktx:1.2.0")
    implementation("androidx.appcompat:appcompat:1.2.0-alpha03")
    implementation("androidx.appcompat:appcompat-resources:1.2.0-alpha03")
    implementation("androidx.activity:activity:1.2.0-alpha02")
    implementation("androidx.activity:activity-ktx:1.2.0-alpha02")
    implementation("androidx.fragment:fragment:1.3.0-alpha02")
    implementation("androidx.fragment:fragment-ktx:1.3.0-alpha02")
    implementation("com.google.android.support:wearable:2.5.0")

    implementation("androidx.wear:wear:1.0.0")
    implementation("com.google.android.material:material:1.1.0")
    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

    compileOnly("com.google.android.wearable:wearable:2.5.0")
}
