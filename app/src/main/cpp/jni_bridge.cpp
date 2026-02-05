/**
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>
#include "logging_macros.h"
#include "LiveEffectEngine.h"
#include <jalv/jalv.h>
#include <lilv/lilv.h>

static const int kOboeApiAAudio = 0;
static const int kOboeApiOpenSLES = 1;

static LiveEffectEngine *engine = nullptr;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_org_acoustixaudio_opiqo_opiqo_kitty_AudioEngine_create(JNIEnv *env, jclass) {
    if (engine == nullptr) {
        engine = new LiveEffectEngine();
    }

    return (engine != nullptr) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_org_acoustixaudio_opiqo_opiqo_kitty_AudioEngine_delete(JNIEnv *env,
                                                               jclass) {
    if (engine) {
        engine->setEffectOn(false);
        delete engine;
        engine = nullptr;
    }
}

JNIEXPORT jboolean JNICALL
Java_org_acoustixaudio_opiqo_opiqo_kitty_AudioEngine_setEffectOn(
    JNIEnv *env, jclass, jboolean isEffectOn) {
    if (engine == nullptr) {
        LOGE(
            "Engine is null, you must call createEngine before calling this "
            "method");
        return JNI_FALSE;
    }

    return engine->setEffectOn(isEffectOn) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_org_acoustixaudio_opiqo_opiqo_kitty_AudioEngine_setRecordingDeviceId(
    JNIEnv *env, jclass, jint deviceId) {
    if (engine == nullptr) {
        LOGE(
            "Engine is null, you must call createEngine before calling this "
            "method");
        return;
    }

    engine->setRecordingDeviceId(deviceId);
}

JNIEXPORT void JNICALL
Java_org_acoustixaudio_opiqo_opiqo_kitty_AudioEngine_setPlaybackDeviceId(
    JNIEnv *env, jclass, jint deviceId) {
    if (engine == nullptr) {
        LOGE(
            "Engine is null, you must call createEngine before calling this "
            "method");
        return;
    }

    engine->setPlaybackDeviceId(deviceId);
}

JNIEXPORT jboolean JNICALL
Java_org_acoustixaudio_opiqo_opiqo_kitty_AudioEngine_setAPI(JNIEnv *env,
                                                               jclass type,
                                                               jint apiType) {
    if (engine == nullptr) {
        LOGE(
            "Engine is null, you must call createEngine "
            "before calling this method");
        return JNI_FALSE;
    }

    oboe::AudioApi audioApi;
    switch (apiType) {
        case kOboeApiAAudio:
            audioApi = oboe::AudioApi::AAudio;
            break;
        case kOboeApiOpenSLES:
            audioApi = oboe::AudioApi::OpenSLES;
            break;
        default:
            LOGE("Unknown API selection to setAPI() %d", apiType);
            return JNI_FALSE;
    }

    return engine->setAudioApi(audioApi) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL
Java_org_acoustixaudio_opiqo_opiqo_kitty_AudioEngine_isAAudioRecommended(
    JNIEnv *env, jclass type) {
    if (engine == nullptr) {
        LOGE(
            "Engine is null, you must call createEngine "
            "before calling this method");
        return JNI_FALSE;
    }
    return engine->isAAudioRecommended() ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL
Java_org_acoustixaudio_opiqo_opiqo_kitty_AudioEngine_native_1setDefaultStreamValues(JNIEnv *env,
                                               jclass type,
                                               jint sampleRate,
                                               jint framesPerBurst) {
    oboe::DefaultStreamValues::SampleRate = (int32_t) sampleRate;
    oboe::DefaultStreamValues::FramesPerBurst = (int32_t) framesPerBurst;
}
} // extern "C"


extern "C"
JNIEXPORT void JNICALL
Java_org_acoustixaudio_opiqo_opiqo_kitty_AudioEngine_test(JNIEnv *env, jclass clazz, jstring dir) {
    std::string path;
    if (dir != nullptr) {
        const char* cstr = env->GetStringUTFChars(dir, nullptr);
        if (cstr) {
            path.assign(cstr);
            env->ReleaseStringUTFChars(dir, cstr);
        }
    } else {
        LOGE("[test] path is null");
        return ;
    }

    LilvWorld* world = lilv_world_new();
    LilvNode* lv2_path = lilv_new_file_uri(world, NULL, path.c_str());
    LOGD ("[test] LV2 path set to %s", lilv_node_as_uri(lv2_path));

    lilv_world_set_option(world, LILV_OPTION_LV2_PATH, lv2_path);
    lilv_world_load_all(world);
    const LilvPlugins* plugins = lilv_world_get_all_plugins(world);
    LILV_FOREACH (plugins, i, plugins) {
        const LilvPlugin* p = lilv_plugins_get(plugins, i);
        LOGD("[test] plugin %s\n", lilv_node_as_uri(lilv_plugin_get_uri(p)));
    }
}
