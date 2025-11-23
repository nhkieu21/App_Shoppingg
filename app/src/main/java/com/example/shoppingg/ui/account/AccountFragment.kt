package com.example.shoppingg.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.shoppingg.R
import com.example.shoppingg.data.SessionManager

class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())

        if (!sessionManager.isLoggedIn()) {
            findNavController().navigate(R.id.loginFragment)
        }

        view.findViewById<View>(R.id.btnMyOrders).setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_myOrdersFragment)
        }

        view.findViewById<View>(R.id.btnChangePassword).setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_changePassword)
        }

        view.findViewById<View>(R.id.btnLogout).setOnClickListener {
            sessionManager.saveLoginState(false)
            Toast.makeText(requireContext(), "Logout successful!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)

        }
    }
}


