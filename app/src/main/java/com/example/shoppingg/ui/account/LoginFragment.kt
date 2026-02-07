package com.example.shoppingg.ui.account

import android.R.attr.text
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.shoppingg.R
import com.example.shoppingg.data.SessionManager
import com.example.shoppingg.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val defaultColor by lazy { ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray) }
    private val errorColor by lazy { ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_dark) }

    private var isEmailTouched = false
    private var isPasswordTouched = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailFromRegister = arguments?.getString("email")
        val passwordFromRegister = arguments?.getString("password")

        if (!emailFromRegister.isNullOrEmpty()) binding.etEmail.setText(emailFromRegister)
        if (!passwordFromRegister.isNullOrEmpty()) binding.etPassword.setText(passwordFromRegister)

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        setupTextWatchers()
        setupFocusListeners()

        binding.btnLogin.setOnClickListener {
            validateAllInputsAndLogin()
        }
    }

    private fun resetInputErrors() {
        binding.etEmail.backgroundTintList = defaultColor
        binding.tvEmailError.text = " "
        binding.etPassword.backgroundTintList = defaultColor
        binding.tvPasswordError.text = " "
    }

    private fun showInvalidError() {
        val errorMessage = "Invalid email or password!"
        binding.tvEmailError.text = errorMessage
        binding.etEmail.backgroundTintList = errorColor
        binding.tvPasswordError.text = errorMessage
        binding.etPassword.backgroundTintList = errorColor
    }


    private fun setupTextWatchers() {
        binding.etEmail.addTextChangedListener(createTextWatcher(R.id.etEmail))
        binding.etPassword.addTextChangedListener(createTextWatcher(R.id.etPassword))
    }

    private fun createTextWatcher(id: Int) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            resetInputErrors()
        }
        override fun afterTextChanged(s: Editable?) {
            when (id) {
                R.id.etEmail -> if (isEmailTouched) validateEmail()
                R.id.etPassword -> if (isPasswordTouched) validatePassword()
            }
        }
    }

    private fun setupFocusListeners() {
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isEmailTouched = true
                resetInputErrors()
            }
            else validateEmail()
        }
        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                isPasswordTouched = true
                resetInputErrors()
            }
            else validatePassword()
        }
    }
    private fun validateEmail(): Boolean {
        val rawText = binding.etEmail.text.toString()
        val text = rawText.trim()
        binding.tvEmailError.visibility = View.VISIBLE
        return when {
            rawText.contains(" ") -> {
                binding.tvEmailError.text = "Email cannot contain spaces"
                binding.etEmail.backgroundTintList = errorColor
                false
            }
            text.isEmpty() -> {
                binding.tvEmailError.text = "Email cannot be empty"
                binding.etEmail.backgroundTintList = errorColor
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(text).matches() -> {
                binding.tvEmailError.text = "Invalid email format"
                binding.etEmail.backgroundTintList = errorColor
                false
            }
            else -> {
                binding.tvEmailError.text = " "
                binding.etEmail.backgroundTintList = defaultColor
                true
            }
        }
    }

    private fun validatePassword(): Boolean {
        val text = binding.etPassword.text.toString().trim()
        return when {
            text.isEmpty() -> {
                binding.tvPasswordError.text = "Password cannot be empty"
                binding.etPassword.backgroundTintList = errorColor
                false
            }
            text.length < 8 -> {
                binding.tvPasswordError.text = "Password must be at least 8 characters"
                binding.etPassword.backgroundTintList = errorColor
                false
            }
            else -> {
                binding.tvPasswordError.text = " "
                binding.etPassword.backgroundTintList = defaultColor
                true
            }
        }
    }

    private fun validateAllInputsAndLogin() {
        isEmailTouched = true
        isPasswordTouched = true

        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()

        if (isEmailValid && isPasswordValid) {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val sessionManager = SessionManager(requireContext())
            val savedEmail = sessionManager.getUserEmail()
            val savedPassword = sessionManager.getUserPassword()

            if (email == savedEmail && password == savedPassword) {
                sessionManager.saveLoginState(true)
                Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.navigation_home)
            } else {
                showInvalidError()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
