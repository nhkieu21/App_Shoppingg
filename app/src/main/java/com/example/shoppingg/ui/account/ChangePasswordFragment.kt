package com.example.shoppingg.ui.account

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.shoppingg.R
import com.example.shoppingg.data.SessionManager
import com.example.shoppingg.databinding.FragmentChangePasswordBinding

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val defaultColor by lazy {
        ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
    }
    private val errorColor by lazy {
        ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_dark)
    }

    private var isPasswordTouched = false
    private var isNewPasswordTouched = false
    private var isConfirmNewPasswordTouched = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextWatchers()
        setupFocusListeners()

        binding.btnUpdate.setOnClickListener {
            validateAndChangePassword()
        }
    }

    private fun setupTextWatchers() {
        binding.etPassword.addTextChangedListener(createTextWatcher(R.id.etPassword))
        binding.etNewPassword.addTextChangedListener(createTextWatcher(R.id.etNewPassword))
        binding.etConfirmNewPassword.addTextChangedListener(createTextWatcher(R.id.etConfirmNewPassword))
    }

    private fun createTextWatcher(id: Int) = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            when (id) {
                R.id.etPassword -> if (isPasswordTouched) validatePassword()
                R.id.etNewPassword -> if (isNewPasswordTouched) validateNewPassword()
                R.id.etConfirmNewPassword -> if (isConfirmNewPasswordTouched) validateConfirmNewPassword()
            }
        }
    }

    private fun setupFocusListeners() {
        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) isPasswordTouched = true else validatePassword()
        }
        binding.etNewPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) isNewPasswordTouched = true else validateNewPassword()
        }
        binding.etConfirmNewPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) isConfirmNewPasswordTouched = true else validateConfirmNewPassword()
        }
    }

    private fun validatePassword(): Boolean {
        val text = binding.etPassword.text.toString().trim()
        val session = SessionManager(requireContext())
        val storedPassword = session.getUserPassword() ?: ""

        binding.tvPasswordError.visibility = View.VISIBLE

        return when {
            text.isEmpty() -> {
                binding.tvPasswordError.text = "Current password cannot be empty"
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

    private fun validateNewPassword(): Boolean {
        val newPass = binding.etNewPassword.text.toString().trim()
        val session = SessionManager(requireContext())
        val storedPassword = session.getUserPassword() ?: ""

        binding.tvNewPasswordError.visibility = View.VISIBLE

        return when {
            newPass.isEmpty() -> {
                binding.tvNewPasswordError.text = "New password cannot be empty"
                binding.etNewPassword.backgroundTintList = errorColor
                false
            }
            newPass.length < 8 -> {
                binding.tvNewPasswordError.text = "Password must be at least 8 characters"
                binding.etNewPassword.backgroundTintList = errorColor
                false
            }
            else -> {
                binding.tvNewPasswordError.text = " "
                binding.etNewPassword.backgroundTintList = defaultColor
                true
            }
        }
    }

    private fun validateConfirmNewPassword(): Boolean {
        val newPass = binding.etNewPassword.text.toString().trim()
        val confirm = binding.etConfirmNewPassword.text.toString().trim()

        binding.tvConfirmNewPasswordError.visibility = View.VISIBLE

        return when {
            confirm.isEmpty() -> {
                binding.tvConfirmNewPasswordError.text = "Please confirm password"
                binding.etConfirmNewPassword.backgroundTintList = errorColor
                false
            }
            confirm.length < 8 -> {
                binding.tvConfirmNewPasswordError.text = "Confirm password must be at least 8 characters"
                binding.etConfirmNewPassword.backgroundTintList = errorColor
                false
            }
            confirm != newPass -> {
                binding.tvConfirmNewPasswordError.text = "Passwords do not match"
                binding.etConfirmNewPassword.backgroundTintList = errorColor
                false
            }
            else -> {
                binding.tvConfirmNewPasswordError.text = " "
                binding.etConfirmNewPassword.backgroundTintList = defaultColor
                true
            }
        }
    }

    private fun validateAndChangePassword() {
        isPasswordTouched = true
        isNewPasswordTouched = true
        isConfirmNewPasswordTouched = true

        val validPassword = validatePassword()
        val validNewPassword = validateNewPassword()
        val validConfirm = validateConfirmNewPassword()

        if (!validPassword || !validNewPassword || !validConfirm) {
            return
        }

        val session = SessionManager(requireContext())
        val email = session.getUserEmail() ?: ""
        val password = session.getUserPassword() ?:""
        val currentPassword = binding.etPassword.text.toString().trim()
        val newPass = binding.etNewPassword.text.toString().trim()

        if (password != currentPassword){
            binding.tvPasswordError.text = "Current password is incorrect"
            binding.etPassword.backgroundTintList = errorColor
            return
        } else if (password == newPass){
            binding.tvNewPasswordError.text = "New password must be different from current password"
            binding.etNewPassword.backgroundTintList = errorColor
            return
        }

        session.saveUser(email, newPass)

        Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()

        findNavController().navigate(R.id.accountFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
