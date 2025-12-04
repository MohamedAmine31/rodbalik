package com.example.rodbalek_frontend.ui.forget_password

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.rodbalek_frontend.api.RetrofitClient
import com.example.rodbalek_frontend.data.model.login.ChangePasswordRequest
import com.example.rodbalek_frontend.data.repository.ForgetPasswordRepository
import com.example.rodbalek_frontend.databinding.ActivityNewPasswordBinding
import com.example.rodbalek_frontend.ui.auth.LoginActivity
import com.example.rodbalek_frontend.viewmodel.ChangePasswordViewModel
import com.example.rodbalek_frontend.viewmodel.ChangePasswordViewModelFactory
import org.json.JSONObject

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewPasswordBinding
    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("email") ?: ""

        val apiService = RetrofitClient.getPublicInstance()
        val repository = ForgetPasswordRepository(apiService)
        val factory = ChangePasswordViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ChangePasswordViewModel::class.java]

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.changePasswordResult.observe(this) { result ->
            result.onSuccess { response ->
                Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }.onFailure { t ->
                val message = t.message
                try {
                    val errorJson = JSONObject(message ?: "")
                    val errorMessage = errorJson.optString("message", "Erreur inconnue")
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Erreur serveur", Toast.LENGTH_LONG).show()
                }
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnConfirmer.isEnabled = !isLoading
        }
    }

    private fun setupListeners() {
        binding.btnConfirmer.setOnClickListener {
            val newPassword = binding.newPassword.text.toString().trim()
            val confirmPassword = binding.confirmPassword.text.toString().trim()

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = ChangePasswordRequest(
                email = email,
                new_password = newPassword,
                confirm_password = confirmPassword
            )
            
            viewModel.changePassword(request)
        }
    }
}