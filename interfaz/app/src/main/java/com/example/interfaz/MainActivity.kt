package com.example.interfaz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.interfaz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private lateinit var promptInfo: BiometricPrompt.PromptInfo

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val executor = ContextCompat.getMainExecutor(this)

    val biometricPrompt = BiometricPrompt(this, executor,
      object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
          super.onAuthenticationError(errorCode, errString)
          Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
          super.onAuthenticationSucceeded(result)

          Toast.makeText(applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
          val intent = Intent(applicationContext, Chatbot::class.java)
          startActivity(intent)
        }

        override fun onAuthenticationFailed() {
          super.onAuthenticationFailed()
          Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
        }
      }
    )

    promptInfo = BiometricPrompt.PromptInfo.Builder()
      .setTitle("Biometric login for my app")
      .setSubtitle("Log in using your biometric credential")
      .setNegativeButtonText("Use account password")
      .build()

    binding.imageView2.setOnClickListener {
      biometricPrompt.authenticate(promptInfo)
    }
  }
}
