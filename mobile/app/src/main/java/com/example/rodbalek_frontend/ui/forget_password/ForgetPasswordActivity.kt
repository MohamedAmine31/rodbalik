package com.example.rodbalek_frontend.ui.forget_password

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.rodbalek_frontend.api.RetrofitClient
import com.example.rodbalek_frontend.data.repository.ForgetPasswordRepository
import com.example.rodbalek_frontend.databinding.ActivityForgetPasswordBinding
import com.example.rodbalek_frontend.viewmodel.ForgetPasswordViewModel
import com.example.rodbalek_frontend.viewmodel.ForgetPasswordViewModelFactory
import org.json.JSONObject

class ForgetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetPasswordBinding
    
    private val viewModel: ForgetPasswordViewModel by viewModels { 
        val apiService = RetrofitClient.getPublicInstance()
        val repository = ForgetPasswordRepository(apiService)
        ForgetPasswordViewModelFactory(repository) 
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.forgetPasswordResult.observe(this) { result ->
            result.onSuccess { response ->
                val message = response.message
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                val intent = Intent(this, ResetPasswordActivity::class.java)
                intent.putExtra("email", binding.email.text.toString().trim())
                startActivity(intent)
                finish()
            }.onFailure { t ->
                handleError(t)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
             binding.confirmer.isEnabled = !isLoading
        }
    }

    private fun handleError(t: Throwable) {
         val message = t.message
         try {
            val errorJson = JSONObject(message ?: "")
            val errorMessage = errorJson.optString("message", "Email introuvable")
             Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
         } catch (e: Exception) {
             Log.e("ForgetPassword", "Erreur API : ${t.message}")
             Toast.makeText(this, "Email introuvable ou erreur serveur", Toast.LENGTH_LONG).show()
         }
    }

    private fun setupListeners() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                 val email = binding.email.text.toString().trim()
                 binding.confirmer.isEnabled = email.isNotEmpty()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.email.addTextChangedListener(textWatcher)

        binding.confirmer.setOnClickListener {
            val email = binding.email.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer votre e-mail", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.forgetPassword(email)
        }
    }
}