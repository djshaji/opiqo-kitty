# opiqo-kitty
<img width="512" height="512" alt="list" src="https://github.com/user-attachments/assets/e37ffa61-18db-4ca3-9349-7d2fdbeafdaa" />

## Overview
Android app that uses a native audio engine (C++/JNI) with Oboe and bundled LV2 tooling to load and run audio effects.

## Requirements
- Android Studio with Android SDK (compileSdk 36, minSdk 34, targetSdk 36).
- Android NDK + CMake 3.22.1 (used by `app/src/main/cpp/CMakeLists.txt`).
- Gradle wrapper included.

## Build and run
Open the project in Android Studio and run the `app` configuration, or use Gradle:

```bash
./gradlew :app:assembleDebug
```

## Tests
Unit/instrumented test dependencies are configured. Run with:

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedAndroidTest
```

## Project structure
- `app/src/main/java/org/acoustixaudio/opiqo/opiqo/kitty/` - Android UI + JNI glue (`MainActivity`, `AudioEngine`).
- `app/src/main/cpp/` - Native audio engine and JNI (`kitty.cpp`, `LiveEffectEngine.cpp`, `jni_bridge.cpp`).
- `app/src/main/cpp/include/lv2/` - LV2 headers used by the native layer.
- `app/src/main/cpp/CMakeLists.txt` - Native build config linking prebuilt libs and Oboe.

## Native libraries
The CMake configuration links prebuilt static libraries from `app/src/main/libs/${ANDROID_ABI}/` (e.g., `libjalv`, `liblilv`, `libjack`). Ensure these are present for each target ABI.
