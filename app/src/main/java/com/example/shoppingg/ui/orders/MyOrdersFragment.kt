package com.example.shoppingg.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingg.databinding.FragmentMyOrdersBinding
import com.example.shoppingg.data.OrderManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.fragment.findNavController
import com.example.shoppingg.R

class MyOrdersFragment : Fragment() {
    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var ordersAdapter: MyOrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ordersAdapter = MyOrdersAdapter(OrderManager.getOrders())
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = ordersAdapter

        updateOrdersUI(OrderManager.getOrders())

        binding.btnGoToShop.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            bottomNav.selectedItemId = R.id.navigation_home }
    }

    private fun updateOrdersUI(orders: List<Order>) {
        if (orders.isEmpty()) {
            binding.emptyCartLayout.visibility = View.VISIBLE
            binding.rvOrders.visibility = View.GONE
        } else {
            binding.emptyCartLayout.visibility = View.GONE
            binding.rvOrders.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        updateOrdersUI(OrderManager.getOrders())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
