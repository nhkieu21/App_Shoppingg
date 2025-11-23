package com.example.shoppingg.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingg.R
import com.example.shoppingg.data.CartManager
import com.example.shoppingg.data.OrderManager
import com.example.shoppingg.databinding.FragmentCartBinding
import com.example.shoppingg.ui.orders.Order
import com.example.shoppingg.ui.orders.OrderItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        // Setup RecyclerView
        adapter = CartAdapter(
            CartManager.cartItems.toMutableList()
        ) {
            // Callback
            updateCartSummary()
            checkEmptyCart()
        }
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = adapter


        // Checkout
        binding.buttonCheckout.setOnClickListener {
            showBillAndClearCart()
        }

        //Go to Shop
        binding.btnGoToShop.setOnClickListener {
            findNavController().navigate(R.id.navigation_home)
            val bottomNav =
                requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            bottomNav.selectedItemId = R.id.navigation_home
        }

        updateCartSummary()
        checkEmptyCart()

        return binding.root
    }

    private fun checkEmptyCart() {
        if (CartManager.cartItems.isEmpty()) {
            binding.emptyCartLayout.visibility = View.VISIBLE
            binding.recyclerViewCart.visibility = View.GONE
            binding.buttonCheckout.visibility = View.GONE
        } else {
            binding.emptyCartLayout.visibility = View.GONE
            binding.recyclerViewCart.visibility = View.VISIBLE
            binding.buttonCheckout.visibility = View.VISIBLE
        }
    }

    fun formatCurrencyVN(amount: Int): String {
        val vn = Locale("vi", "VN")
        val formatter = NumberFormat.getInstance(vn)
        return formatter.format(amount)
    }

    private fun showBillAndClearCart() {
        val count = CartManager.getItemCount()
        val total = CartManager.getTotalPrice()

        // Bill
        val billDetails = buildString {
            append("Your Order:\n\n")
            CartManager.cartItems.forEach {
                append("- ${it.product.name} x ${it.quantity} = ${formatCurrencyVN(it.product.price * it.quantity)}₫\n")
            }
            append("\nTotal items: $count")
            append("\nTotal price: ${formatCurrencyVN(total)}₫")
        }

        // AlertDialog
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Thank You For Your Order")
            .setMessage(billDetails)
            .setPositiveButton("OK") { dialog, _ ->
                Toast.makeText(requireContext(), "Checkout successful!", Toast.LENGTH_SHORT).show()

                val items = CartManager.cartItems

                val totalItems = items.sumOf { it.quantity }
                val totalPrice = items.sumOf { it.product.price * it.quantity }

                val order = Order(
                    orderId = System.currentTimeMillis().toString(),
                    total = formatCurrencyVN(totalPrice),
                    totalItems = totalItems,
                    items = items.map {
                        OrderItem(
                            name = it.product.name,
                            image = it.product.image,
                            quantity = it.quantity
                        )
                    }
                )

                OrderManager.addOrder(order)

                CartManager.clear()
                updateCartSummary()
                checkEmptyCart()
                dialog.dismiss()
            }
            .show()
    }


    override fun onResume() {
        super.onResume()
        adapter.updateData(CartManager.cartItems.toMutableList())
        updateCartSummary()
        checkEmptyCart()
    }

    private fun updateCartSummary() {
        val count = CartManager.getItemCount()
        val total = CartManager.getTotalPrice()
        binding.tvCartSummary.text = "Items: $count | Total: ${formatCurrencyVN(total)}₫"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
