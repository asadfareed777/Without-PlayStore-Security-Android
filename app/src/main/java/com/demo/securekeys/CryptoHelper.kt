package com.demo.securekeys

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Example consumer of the native constants: AES/CBC/PKCS5 using
 * AppConstants.SECRET (key) and AppConstants.IV.
 */
object CryptoHelper {

    private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"

    fun encrypt(plain: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, key(), iv())
        }
        return Base64.encodeToString(cipher.doFinal(plain.toByteArray()), Base64.NO_WRAP)
    }

    fun decrypt(base64: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, key(), iv())
        }
        return String(cipher.doFinal(Base64.decode(base64, Base64.NO_WRAP)))
    }

    private fun key() = SecretKeySpec(AppConstants.SECRET_BYTES, "AES")
    private fun iv() = IvParameterSpec(AppConstants.IV_BYTES)
}
