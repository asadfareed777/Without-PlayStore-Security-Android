package com.demo.securekeys

/**
 * App-wide constants, populated ONCE from native code at startup
 * (see [SecureKeysApp.onCreate]).
 *
 * Usage anywhere in the app:
 *     AppConstants.BASE_URL
 *     AppConstants.IV_BYTES
 *     AppConstants.SECRET_BYTES
 */
object AppConstants {

    lateinit var BASE_URL: String
        private set

    lateinit var IV: String
        private set

    lateinit var SECRET: String
        private set

    /** Raw bytes — handy for javax.crypto (IvParameterSpec / SecretKeySpec). */
    val IV_BYTES: ByteArray get() = IV.toByteArray(Charsets.UTF_8)
    val SECRET_BYTES: ByteArray get() = SECRET.toByteArray(Charsets.UTF_8)

    @Volatile
    var isInitialized: Boolean = false
        private set

    /** Pulls every constant from C++. Safe to call more than once. */
    @Synchronized
    fun init() {
        if (isInitialized) return
        BASE_URL = NativeKeys.nativeBaseUrl()
        IV = NativeKeys.nativeIv()
        SECRET = NativeKeys.nativeSecret()
        isInitialized = true
    }
}
