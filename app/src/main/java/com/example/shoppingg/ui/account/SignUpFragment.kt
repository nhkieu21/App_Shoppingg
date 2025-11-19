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
import com.example.shoppingg.databinding.FragmentRegisterBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val defaultColor by lazy { ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray) }
    private val errorColor by lazy { ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_dark) }

    private var isFullNameTouched = false
    private var isEmailTouched = false
    private var isPasswordTouched = false
    private var isConfirmPasswordTouched = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            validateAllInputsAndRegister()
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        setupTextWatchers()
        setupFocusListeners()
    }
    private fun setupTextWatchers() {
        binding.etFullName.addTextChangedListener(createTextWatcher(R.id.etFullName))
        binding.etEmail.addTextChangedListener(createTextWatcher(R.id.etEmail))
        binding.etPassword.addTextChangedListener(createTextWatcher(R.id.etPassword))
        binding.etConfirmPassword.addTextChangedListener(createTextWatcher(R.id.etConfirmPassword))
    }

    private fun createTextWatcher(id: Int) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            when (id) {
                R.id.etFullName -> if (isFullNameTouched) validateFullName()
                R.id.etEmail -> if (isEmailTouched) validateEmail()
                R.id.etPassword -> if (isPasswordTouched) validatePassword()
                R.id.etConfirmPassword -> if (isConfirmPasswordTouched) validateConfirmPassword()
            }
        }
    }

    private fun setupFocusListeners() {
        binding.etFullName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) isFullNameTouched = true
            else validateFullName()
        }
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) isEmailTouched = true
            else validateEmail()
        }
        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) isPasswordTouched = true
            else validatePassword()
        }
        binding.etConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) isConfirmPasswordTouched = true
            else validateConfirmPassword()
        }
    }

    private fun validateFullName(): Boolean {
        val text = binding.etFullName.text.toString().trim()
        binding.tvNameError.visibility = View.VISIBLE
        return when {
            text.isEmpty() -> {
                binding.tvNameError.text = "Full name cannot be empty"
                binding.etFullName.backgroundTintList = errorColor
                false
            }
            else -> {
                binding.tvNameError.text = " "
                binding.etFullName.backgroundTintList = defaultColor
                true
            }
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
        binding.tvPasswordError.visibility = View.VISIBLE
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

    private fun validateConfirmPassword(): Boolean {
        val password = binding.etPassword.text.toString().trim()
        val confirm = binding.etConfirmPassword.text.toString().trim()
        binding.tvConfirmPasswordError.visibility = View.VISIBLE
        return when {
            confirm.isEmpty() -> {
                binding.tvConfirmPasswordError.text = "Please confirm your password"
                binding.etConfirmPassword.backgroundTintList = errorColor
                false
            }
            confirm.length < 8 -> {
                binding.tvConfirmPasswordError.text = "Confirm password must be at least 8 characters"
                binding.etConfirmPassword.backgroundTintList = errorColor
                false
            }
            confirm != password -> {
                binding.tvConfirmPasswordError.text = "Passwords do not match"
                binding.etConfirmPassword.backgroundTintList = errorColor
                false
            }
            else -> {
                binding.tvConfirmPasswordError.text = " "
                binding.etConfirmPassword.backgroundTintList = defaultColor
                true
            }
        }
    }

    private fun validateAllInputsAndRegister() {
        isFullNameTouched = true
        isEmailTouched = true
        isPasswordTouched = true
        isConfirmPasswordTouched = true

        val isFullNameValid = validateFullName()
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        val isConfirmValid = validateConfirmPassword()

        if (isFullNameValid && isEmailValid && isPasswordValid && isConfirmValid) {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val sessionManager = SessionManager(requireContext())
            sessionManager.saveUser(email, password)
            sessionManager.saveLoginState(false)

            Toast.makeText(requireContext(), "Registration successful!", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
