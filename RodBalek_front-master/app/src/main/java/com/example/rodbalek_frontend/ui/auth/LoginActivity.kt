package com.example.rodbalek_frontend.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.rodbalek_frontend.R
import com.example.rodbalek_frontend.Navigation
import com.example.rodbalek_frontend.data.model.login.LoginRequest
import com.example.rodbalek_frontend.databinding.ActivityLoginBinding
import com.example.rodbalek_frontend.ui.forget_password.ForgetPasswordActivity
import com.example.rodbalek_frontend.utils.SessionManager
import com.example.rodbalek_frontend.viewmodel.AuthViewModel
import com.example.rodbalek_frontend.viewmodel.AuthViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: AuthViewModel by viewModels { AuthViewModelFactory(this) }

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { tokenResponse ->
                val accessToken = tokenResponse.access
                val refreshToken = tokenResponse.refresh
                val userId = tokenResponse.user_id

                SessionManager.saveAuth(this@LoginActivity, accessToken, refreshToken, userId)
                Log.d("LOGIN_DEBUG", "Login OK - UserID: $userId")
                Toast.makeText(this@LoginActivity, "Connexion réussie", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@LoginActivity, Navigation::class.java)
                intent.putExtra("user_id", userId)
                startActivity(intent)
                finish()
            }.onFailure { t ->
                Toast.makeText(this@LoginActivity, "Identifiants incorrects ", Toast.LENGTH_SHORT).show()
                binding.emailPhone.error = "Vérifiez vos identifiants"
                binding.password.error = "Vérifiez vos identifiants"
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
            binding.loginButton.isEnabled = !isLoading
        }
    }

    private fun setupListeners() {
        val togglePassword: ImageButton = binding.root.findViewById(R.id.togglePassword)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val emailPhone = binding.emailPhone.text.toString().trim()
                val password = binding.password.text.toString()
                binding.loginButton.isEnabled = emailPhone.isNotEmpty() && password.isNotEmpty()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.emailPhone.addTextChangedListener(textWatcher)
        binding.password.addTextChangedListener(textWatcher)

        binding.loginButton.setOnClickListener {
            val username = binding.emailPhone.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val loginRequest = LoginRequest(username, password)
            viewModel.login(loginRequest)
        }

        togglePassword.isEnabled = true
        togglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                togglePassword.setImageResource(R.drawable.eye)
            } else {
                binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
                togglePassword.setImageResource(R.drawable.blind)
            }
            binding.password.setSelection(binding.password.text?.length ?: 0)
        }

        binding.linkSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }
    }
}