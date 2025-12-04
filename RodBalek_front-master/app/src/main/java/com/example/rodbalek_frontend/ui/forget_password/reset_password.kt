package com.example.rodbalek_frontend.ui.forget_password

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.rodbalek_frontend.api.RetrofitClient
import com.example.rodbalek_frontend.data.repository.ForgetPasswordRepository
import com.example.rodbalek_frontend.databinding.ActivityResetPasswordBinding
import com.example.rodbalek_frontend.viewmodel.ResetPasswordViewModel
import com.example.rodbalek_frontend.viewmodel.ResetPasswordViewModelFactory
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var email: String

    private val viewModel: ResetPasswordViewModel by viewModels {
        val apiService = RetrofitClient.getPublicInstance()
        val repository = ForgetPasswordRepository(apiService)
        ResetPasswordViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("email") ?: ""

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.verifyCodeResult.observe(this) { result ->
            result.onSuccess { response ->
                val message = response.message
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                val intent = Intent(this, NewPasswordActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
                finish()
            }.onFailure { t ->
                 val message = t.message
                 try {
                     val errorJson = JSONObject(message ?: "")
                     val errorMessage = errorJson.optString("message", "Code invalide ou expiré")
                     Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                 } catch (e: Exception) {
                     Toast.makeText(this, "Erreur serveur", Toast.LENGTH_LONG).show()
                 }
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            // Show/hide loader
        }

        viewModel.timerText.observe(this) { time ->
            binding.timerText.text = time
        }

        viewModel.isTimerExpired.observe(this) { isExpired ->
             if (isExpired) {
                 binding.confirmer.isEnabled = false
                 Toast.makeText(this, "Le code a expiré, veuillez en demander un nouveau.", Toast.LENGTH_LONG).show()
             }
        }
    }

    private fun setupListeners() {
        binding.code.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isExpired = viewModel.isTimerExpired.value ?: false
                if (!isExpired) {
                    binding.confirmer.isEnabled = binding.code.text.toString().trim().isNotEmpty()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.confirmer.setOnClickListener {
            val code = binding.code.text.toString().trim()
            if (code.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer le code de vérification", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.verifyCode(email, code)
        }
    }
}