# TaskBound
#### TaskBound is an Android project that requires specific local files and configurations for a proper setup. This guide outlines the necessary steps to set up the project after cloning it from the GitHub repository.

## Prerequisites
1. Android Studio: Download the latest version from Android Studio.

2. JDK 8 or later: Ensure your system has Java Development Kit installed.

3. Gradle: Use the version specified by Android Studio or sync automatically during setup.

4. Android API Level 24 or higher (minimum SDK version) - Use API 24 for best stability when running the app

## Setup Steps
### 1. Clone the Repository

```bash

git clone https://github.com/marcojalenyu/taskbound.git
cd taskbound
```

### 2. Create Required Files

Some files are excluded from the repository. You must create and configure these files in the appropriate locations:

- ### 2.1 libs.version.toml

  - Create the file at the following path: gradle/libs.versions.toml
    
```toml
[versions]
agp = "8.6.0"
gson = "2.10.1"
jbcrypt = "0.4"
junit = "4.13.2"
junitVersion = "1.2.1"
espressoCore = "3.6.1"
appcompat = "1.7.0"
material = "1.12.0"
activity = "1.9.2"
constraintlayout = "2.1.4"
navigationFragment = "2.6.0"
navigationUi = "2.6.0"
composeThemeAdapter = "1.2.1"
googleGmsGoogleServices = "4.4.2"
firebaseDatabase = "21.0.0"
firebaseAuth = "23.1.0"

[libraries]
junit = { group = "junit", name = "junit", version.ref = "junit" }
gson = { group = "com.google.code.gson", name = "gson", version.ref = "gson" }
jbcrypt = { group = "org.mindrot", name = "jbcrypt", version.ref = "jbcrypt" }
ext-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
activity = { group = "androidx.activity", name = "activity", version.ref = "activity" }
constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }
navigation-fragment = { group = "androidx.navigation", name = "navigation-fragment", version.ref = "navigationFragment" }
navigation-ui = { group = "androidx.navigation", name = "navigation-ui", version.ref = "navigationUi" }
compose-theme-adapter = { group = "com.google.android.material", name = "compose-theme-adapter", version.ref = "composeThemeAdapter" }
firebase-database = { group = "com.google.firebase", name = "firebase-database", version.ref = "firebaseDatabase" }
firebase-auth = { group = "com.google.firebase", name = "firebase-auth", version.ref = "firebaseAuth" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
google-gms-google-services = { id = "com.google.gms.google-services", version.ref = "googleGmsGoogleServices" }
```

- ### 2.2 build.gradle.kts
  - Create this file under the App/Gradle Scripts Folder if missing, with the following content:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.mobdeve.s17.taskbound"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mobdeve.s17.taskbound"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.gson)
    implementation(libs.jbcrypt)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("androidx.work:work-runtime:2.7.1")
}
```
- ### 2.3 google-services.json
  - This file contains sensitive Firebase project configuration and is excluded from the repository. Place it in:

```bash
<project_root>/app/google-services.json
```
  - Please contact the developers if you are interested in knowing about this. 


### 3. Open the Project in Android Studio
Launch Android Studio.
Open the project folder (<project_root>).

### 4. Sync and Configure the Project
Allow Android Studio to sync the project and download any necessary dependencies.
If prompted, install any missing SDK components or tools to match the required API level (minimum API Level 24).

### 5. Install Missing SDK Components or Tools
When prompted.

### 6. Build the Project
Build the project by selecting Build > Make Project from the menu.

### 7. Gradle Sync
Sync Gradle when prompted to ensure all dependencies are correctly resolved.

### 8. Run the Application
Connect a device or use an emulator.
Run the application by pressing the Play button in Android Studio.

