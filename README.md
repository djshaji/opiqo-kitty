# opiqo-kitty
<img width="512" height="512" alt="list" src="https://github.com/user-attachments/assets/e37ffa61-18db-4ca3-9349-7d2fdbeafdaa" />


This is a _port_ of [GxSloopyBlue.lv2](https://github.com/brummer10/GxSloopyBlue.lv2) by the legendary [brummer10](https://github.com/brummer10) 

We love you Hermann 💙
 
Classic Blues Overdrive for Android
______________________________________
Bring a warm, touch‑responsive overdrive to your Android device. opiqo Kitty is inspired by a legendary blues pedal and delivers everything from edge‑of‑clean sparkle to thick, singing sustain. It reacts to your picking dynamics like a real amp, making it perfect for blues, rock, and expressive lead playing.

Highlights
_______________
- Classic blues overdrive character
- Smooth, amp‑like breakup with touch sensitivity
- Wide gain range: grit to rich sustain
- Simple, familiar controls for fast dialing
- Great for practice, recording, and live rigs
______________________________________________
Who it’s for
Players who want a musical, responsive drive that cleans up with your volume knob and blooms when you dig in.
_______________________________________________
Notes
Requires an audio interface or compatible input method for best results
Latency depends on your device and audio setup
_______________________________________________

But what is opiqo really?
opiqo is actually a new LV2 Host Platform Prototype which will power the next generation of Acoustix audio guitar effect pedals.

It is built as an LV2 plugin host using standard libraries, focusing on stability, portability, and modern audio workflows. The app demonstrates a clean, responsive signal path and a solid foundation for hosting LV2 effects on mobile. This prototype explores a scalable architecture for hosting LV2 plugins on mobile devices, with emphasis on reliable audio handling and future expansion.

Prototype status: features may change rapidly
Best results require a low‑latency audio interface
Performance varies by device
This app and platform stack is completely open source. You can create your own apps and use this code absolutely free of charge, even for commercial usage. 

Github: https://github.com/djshaji/opiqo-kitty
Please report bugs to code@acoustixaudio.org

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
