//
// libappcore.so — serves XOR-encoded constants to Kotlin.
//
// Hardening used in this demo:
//   1. Every string (secrets AND JNI class/method names/signatures) is XOR
//      encoded — `strings libappcore.so` shows nothing useful.
//   2. Methods are bound dynamically via RegisterNatives in JNI_OnLoad, so
//      there are no exported `Java_com_demo_...` symbols to grep for.
//   3. Symbols are hidden (-fvisibility=hidden); only JNI_OnLoad is exported.
//   4. Decoded buffers are wiped right after being handed to the JVM.
//
#include <jni.h>
#include <string>
#include "xor_decoder.h"

namespace {

jstring toJString(JNIEnv *env, std::string plain) {
    jstring result = env->NewStringUTF(plain.c_str());
    obf::wipe(plain);
    return result;
}

jstring JNICALL nativeBaseUrl(JNIEnv *env, jobject /*thiz*/) {
    return toJString(env, OBF_DECODE(BASE_URL));
}

jstring JNICALL nativeIv(JNIEnv *env, jobject /*thiz*/) {
    return toJString(env, OBF_DECODE(IV));
}

jstring JNICALL nativeSecret(JNIEnv *env, jobject /*thiz*/) {
    return toJString(env, OBF_DECODE(SECRET));
}

}  // namespace

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void * /*reserved*/) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    // Decode JNI names only for the duration of registration.
    std::string className = OBF_DECODE(JNI_CLASS);
    std::string mBaseUrl  = OBF_DECODE(JNI_M_BASE_URL);
    std::string mIv       = OBF_DECODE(JNI_M_IV);
    std::string mSecret   = OBF_DECODE(JNI_M_SECRET);
    std::string sig       = OBF_DECODE(JNI_SIG_STRING);

    jint rc = JNI_ERR;
    jclass clazz = env->FindClass(className.c_str());
    if (clazz != nullptr) {
        const JNINativeMethod methods[] = {
            {mBaseUrl.c_str(), sig.c_str(), reinterpret_cast<void *>(nativeBaseUrl)},
            {mIv.c_str(),      sig.c_str(), reinterpret_cast<void *>(nativeIv)},
            {mSecret.c_str(),  sig.c_str(), reinterpret_cast<void *>(nativeSecret)},
        };
        if (env->RegisterNatives(clazz, methods,
                                 sizeof(methods) / sizeof(methods[0])) == JNI_OK) {
            rc = JNI_VERSION_1_6;
        }
        env->DeleteLocalRef(clazz);
    }

    obf::wipe(className);
    obf::wipe(mBaseUrl);
    obf::wipe(mIv);
    obf::wipe(mSecret);
    obf::wipe(sig);
    return rc;
}
