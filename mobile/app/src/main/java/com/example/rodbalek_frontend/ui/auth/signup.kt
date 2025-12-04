package com.example.rodbalek_frontend.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.rodbalek_frontend.R
import com.example.rodbalek_frontend.data.model.register.RegisterRequest
import com.example.rodbalek_frontend.databinding.ActivitySignupBinding
import com.example.rodbalek_frontend.viewmodel.AuthViewModel
import com.example.rodbalek_frontend.viewmodel.AuthViewModelFactory
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private val viewModel: AuthViewModel by viewModels { AuthViewModelFactory(this) }

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private var usernameValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.registerResult.observe(this) { result ->
            result.onSuccess {
                binding.errorText.visibility = View.GONE
                Toast.makeText(this, " Compte créé avec succès !", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }.onFailure { t ->
                 val message = t.message
                 try {
                     val json = JSONObject(message ?: "{}")
                     val emailErrors = json.optJSONArray("email")
                     val errorMsg = emailErrors?.getString(0) ?: "Erreur inconnue"
                     binding.errorText.text = errorMsg
                     binding.errorText.visibility = View.VISIBLE
                 } catch (e: Exception) {
                     binding.errorText.text = "Erreur inconnue"
                     binding.errorText.visibility = View.VISIBLE
                 }
            }
        }

        viewModel.usernameCheckResult.observe(this) { result ->
            result.onSuccess { body ->
                usernameValid = body.available
                if (!body.available) {
                    binding.username.error = body.message + " " + body.suggestions.joinToString(", ")
                } else {
                    binding.username.error = null
                }
                validateForm()
            }.onFailure {
                // Log.e("USERNAME_CHECK", "Erreur lors de la vérification du nom d'utilisateur")
            }
        }
        
        viewModel.isLoading.observe(this) { isLoading ->
             if (isLoading) {
                 // Show progress
             } else {
                 // Hide progress
             }
        }
    }
    
    private fun validateForm() {
        val nom = binding.nomInput.text.toString().trim()
        val prenom = binding.prenomInput.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val username = binding.username.text.toString().trim()
        val password = binding.password.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()
        val phone = binding.phone.text.toString().trim()

        val phoneValid = phone.length == 8 && phone.all { it.isDigit() }

        val formValid = nom.isNotEmpty()
                && prenom.isNotEmpty()
                && email.isNotEmpty()
                && username.isNotEmpty()
                && password.isNotEmpty()
                && confirmPassword.isNotEmpty()
                && password == confirmPassword
                && phoneValid
                && usernameValid

        if (formValid) {
            binding.signupButton.isEnabled = true
            binding.signupButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary)
        } else {
            binding.signupButton.isEnabled = false
            binding.signupButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.subtle_dark)
        }
    }


    private fun setupListeners() {
        val togglePassword: ImageButton = binding.root.findViewById(R.id.togglePassword)
        val toggleConfirmPassword: ImageButton = binding.root.findViewById(R.id.toggleConfirmPassword)
        binding.signupButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.subtle_dark)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val username = binding.username.text.toString().trim()
                
                if (binding.username.hasFocus()) {
                   if (username.length >= 3) {
                       viewModel.checkUsername(username)
                   } else if (username.isNotEmpty()) {
                       binding.username.error = "Le nom d'utilisateur doit contenir au moins 3 caractères"
                   } else {
                       binding.username.error = null
                   }
                }

                val nom = binding.nomInput.text.toString().trim()
                val password = binding.password.text.toString()
                val confirmPassword = binding.confirmPassword.text.toString()
                val phone = binding.phone.text.toString().trim()
                val phoneValid = phone.length == 8 && phone.all { it.isDigit() }

                if (phone.isNotEmpty() && !phoneValid) {
                    binding.phone.error = "Le numéro doit contenir exactement 8 chiffres"
                } else {
                    binding.phone.error = null
                }
                if(nom.length<3 && nom.isNotEmpty()){
                    binding.nomInput.error = "le nom doit etre un nom valide (>2)"
                } else {
                    binding.nomInput.error = null
                }
                if(password.length<9 && password.isNotEmpty()){
                    binding.password.error = "le password doit etre >8"
                } else {
                    binding.password.error = null
                }
                if (password.isNotEmpty() && !password.equals(confirmPassword) && confirmPassword.isNotEmpty()) {
                    binding.confirmPassword.error = "les 2 paswword doivent etre identique"
                } else {
                    binding.confirmPassword.error = null
                }

                togglePassword.isEnabled = true
                toggleConfirmPassword.isEnabled = true
                validateForm()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.nomInput.addTextChangedListener(textWatcher)
        binding.prenomInput.addTextChangedListener(textWatcher)
        binding.email.addTextChangedListener(textWatcher)
        binding.username.addTextChangedListener(textWatcher)
        binding.password.addTextChangedListener(textWatcher)
        binding.confirmPassword.addTextChangedListener(textWatcher)
        binding.phone.addTextChangedListener(textWatcher)

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

        toggleConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            if (isConfirmPasswordVisible) {
                binding.confirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                toggleConfirmPassword.setImageResource(R.drawable.eye)
            } else {
                binding.confirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                toggleConfirmPassword.setImageResource(R.drawable.blind)
            }
            binding.confirmPassword.setSelection(binding.confirmPassword.text?.length ?: 0)
        }

        binding.goToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.signupButton.setOnClickListener {
            val nom = binding.nomInput.text.toString().trim()
            val prenom = binding.prenomInput.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val username = binding.username.text.toString().trim()
            val phone = binding.phone.text.toString().trim()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            if (password != confirmPassword) {
                Snackbar.make(binding.root, " Les mots de passe ne correspondent pas.", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (phone.length != 8 || !phone.all { it.isDigit() }) {
                binding.phone.error = "Le numéro doit contenir exactement 8 chiffres"
                return@setOnClickListener
            }

            val user = RegisterRequest(
                username = username,
                email = email,
                password = password,
                last_name = nom,
                first_name = prenom,
                phone = phone,
                role = "CITOYEN"
            )
            viewModel.register(user)
        }
    }
}