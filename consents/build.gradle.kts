plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.moshiIR)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.mavenRelease)
}

val GROUP: String by project
val VERSION_NAME: String by project
group = GROUP
version = VERSION_NAME

android {
    defaultConfig {
        minSdk = 21
        compileSdk = 33
        buildConfigField("String", "SDK_VERSION", """"$version"""")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    namespace = "io.piano.android.consents"
}

kotlin {
    explicitApi()
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

ktlint {
    version.set("0.50.0")
    android.set(true)
}

dependencies {
    implementation(libs.moshi)
    implementation(libs.annotations)
    implementation(libs.timber)

    testImplementation(libs.kotlinJunit)
    testImplementation(libs.mockitoKotlin)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.junit)
}
