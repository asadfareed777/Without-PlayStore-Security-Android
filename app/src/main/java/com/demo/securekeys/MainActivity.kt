package com.demo.securekeys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.securekeys.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var revealed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Constants were already loaded in SecureKeysApp.onCreate()
        binding.tvBaseUrl.text = AppConstants.BASE_URL
        renderSecrets()

        binding.btnToggle.setOnClickListener {
            revealed = !revealed
            renderSecrets()
        }

        binding.btnEncrypt.setOnClickListener {
            val input = binding.etInput.text.toString().ifBlank { "Hello from native keys!" }
            val encrypted = CryptoHelper.encrypt(input)
            val decrypted = CryptoHelper.decrypt(encrypted)
            binding.tvCrypto.text = getString(R.string.crypto_result, encrypted, decrypted)
        }
    }

    private fun renderSecrets() {
        binding.tvIv.text = if (revealed) AppConstants.IV else mask(AppConstants.IV)
        binding.tvSecret.text = if (revealed) AppConstants.SECRET else mask(AppConstants.SECRET)
        binding.btnToggle.setText(if (revealed) R.string.hide else R.string.reveal)
    }

    private fun mask(value: String): String =
        if (value.length <= 4) "****"
        else value.take(2) + "•".repeat(value.length - 4) + value.takeLast(2)
}
