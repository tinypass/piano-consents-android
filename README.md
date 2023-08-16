# Piano Consents SDK for Android
![GitHub](https://img.shields.io/github/license/tinypass/piano-consents-android)
![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/tinypass/piano-consents-android)
![GitHub Workflow Status (branch)](https://img.shields.io/github/actions/workflow/status/tinypass/piano-consents-android/build.yml?branch=main)

Welcome to Piano Consents SDK for Android.

## Getting started
This document details the process of integrating the Piano Consents SDK with your Android application. If you have any questions, don't hesitate to email us at support@piano.io.

### Download the Piano Consents SDK
#### Gradle
The Piano Consents SDK is available as an AAR via MavenCentral. To add dependencies, open your project’s `build.gradle`/`build.gradle.kts` and update `dependencies` block:
```kotlin
dependencies {
    ...
    implementation("io.piano.android:consents:VERSION")
}
```

### Usage
```kotlin
// Configure consents
val pianoConsents = PianoConsents.init(
    context,
    ConsentConfiguration(
        requireConsent = true,                          // `false` means "disable the feature"
        defaultPurposes = mapOf(                        // specify custom purposes for Piano products, `null` means "use default mapping"  
            Product.ID to Purpose.AUDIENCE_MEASUREMENT
        )
    )
)
// Init other Piano SDK (Composer, ID, etc) with the `PianoConsents` instance
Composer.init(..., pianoConsents)
...
// Set consent mode for purpose
pianoConsents.set(Purpose.PERSONAL_RELATIONSHIP, ConsentMode.OPT_OUT)
// Set consent mode for purpose with moving products to the purpose
pianoConsents.set(Purpose.PERSONAL_RELATIONSHIP, ConsentMode.OPT_OUT, Product.COMPOSER, Product.PA)
// Set consent mode for all purposes
pianoConsents.setAll(ConsentMode.OPT_OUT)
// Get current consents
pianoConsents.consents
// Rest consents
pianoConsents.clear()
```

### License
```
Copyright 2023 Piano, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
