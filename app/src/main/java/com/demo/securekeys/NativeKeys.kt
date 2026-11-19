package com.demo.securekeys

/**
 * Thin JNI bridge to libappcore.so.
 *
 * The native side binds these methods with RegisterNatives inside JNI_OnLoad
 * (no `Java_com_demo_...` exported symbols). The method names below must match
 * the XOR-encoded names in the encodeSecrets task (app/build.gradle.kts) -> jniStrings.
 *
 * App code should not call this directly — read [AppConstants] instead.
 */
internal object NativeKeys {

    init {
        System.loadLibrary("appcore")
    }

    external fun nativeBaseUrl(): String
    external fun nativeIv(): String
    external fun nativeSecret(): String
}
