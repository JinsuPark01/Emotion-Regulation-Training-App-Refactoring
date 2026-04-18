plugins {
    // Android 애플리케이션 모듈 플러그인
    alias(libs.plugins.android.application)

    // Kotlin Android 플러그인
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.compose.compiler)

    // Firebase 연동용 Google Services 플러그인
    id("com.google.gms.google-services")

    //Hilt
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    // 앱의 패키지 네임스페이스
    namespace = "com.example.emotionalapp"

    // 컴파일 SDK 버전
    compileSdk = 36

    defaultConfig {
        // 실제 앱 패키지명
        applicationId = "com.example.emotionalapp"

        // 최소 지원 SDK
        minSdk = 24

        // 타겟 SDK
        targetSdk = 36

        // 앱 버전 코드 / 이름
        versionCode = 1
        versionName = "1.0"

        // 안드로이드 테스트 러너
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // 현재는 난독화/최적화 비활성화
            isMinifyEnabled = false

            // Proguard 설정 파일
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        // Java 11 사용
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        // Kotlin JVM 타겟도 11로 맞춤
        jvmTarget = "11"
    }

    buildFeatures {
        // 기존 XML 화면을 아직 사용할 수 있도록 ViewBinding 유지
        viewBinding = true

        // Compose 활성화
        compose = true
    }
}

dependencies {

    // -------------------------
    // Android 기본 라이브러리
    // -------------------------

    // Kotlin Android 확장 함수들
    implementation(libs.androidx.core.ktx)

    // AppCompat 기반 레거시 UI 지원
    implementation(libs.androidx.appcompat)

    // Activity 관련 기본 라이브러리
    implementation(libs.androidx.activity)

    // 기존 XML 레이아웃에서 ConstraintLayout 사용 시 필요
    implementation(libs.androidx.constraintlayout)

    // Material Design 컴포넌트 (XML 기반 UI에서 자주 사용)
    implementation("com.google.android.material:material:1.12.0")

    // RecyclerView (기존 XML 화면에서 리스트 UI 사용 시 필요)
    implementation("androidx.recyclerview:recyclerview:1.3.0")


    // -------------------------
    // Firebase
    // -------------------------

    // Firebase 버전 관리를 BOM으로 통일
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")

    // Firebase Authentication
    implementation("com.google.firebase:firebase-auth")

    // Firestore
    implementation("com.google.firebase:firebase-firestore")

    // Realtime Database
    implementation(libs.firebase.database)


    // -------------------------
    // 차트 / 이미지 / 기타 라이브러리
    // -------------------------

    // 감정 기록 시각화용 차트 라이브러리
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // 이미지 로딩 라이브러리
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Glide annotation processor
    // Kotlin 프로젝트에서는 보통 kapt 사용
    // kapt("com.github.bumptech.glide:compiler:4.16.0")

    // 코루틴 Android 지원
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Firebase Task 등을 코루틴과 연동할 때 사용
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // 앱 프로세스 라이프사이클 감지용
    implementation("androidx.lifecycle:lifecycle-process:2.6.2")

    // Konfetti XML 버전 (기존 XML 화면에서 효과 사용 시)
    implementation("nl.dionsegijn:konfetti-xml:2.0.2")


    // -------------------------
    // Jetpack Compose
    // -------------------------

    // Compose 라이브러리 버전을 BOM으로 통일 관리
    implementation(platform("androidx.compose:compose-bom:2025.08.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.08.00"))

    // Compose를 Activity에서 사용할 때 필요한 의존성
    implementation("androidx.activity:activity-compose:1.10.1")

    // Compose 기본 UI
    implementation("androidx.compose.ui:ui")

    // Compose 레이아웃/기초 컴포넌트
    implementation("androidx.compose.foundation:foundation")

    // Material3 Compose 컴포넌트
    implementation("androidx.compose.material3:material3")

    // Preview 지원
    implementation("androidx.compose.ui:ui-tooling-preview")

    // 디버그 빌드에서 Compose 툴링 지원
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.compose.material:material-icons-extended")

    //
    implementation("androidx.fragment:fragment-ktx:1.8.6")

    //hilt
    implementation("com.google.dagger:hilt-android:2.57.1")
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")

    // -------------------------
    // 테스트
    // -------------------------

    // 단위 테스트
    testImplementation(libs.junit)

    // 안드로이드 테스트
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}