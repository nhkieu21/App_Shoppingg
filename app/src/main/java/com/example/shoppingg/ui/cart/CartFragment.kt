package com.example.shoppingg.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingg.data.CartManager
import com.example.shoppingg.databinding.FragmentCartBinding
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
            // callback khi xóa hoặc thay đổi
            updateCartSummary()
        }
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCart.adapter = adapter


        // nút Checkout
        binding.buttonCheckout.setOnClickListener {
            showBillAndClearCart()
        }

        return binding.root
    }

    fun formatCurrencyVN(amount: Int): String {
        val vn = Locale("vi", "VN")
        val formatter = NumberFormat.getInstance(vn)
        return formatter.format(amount)
    }

    private fun showBillAndClearCart() {

        if (CartManager.cartItems.isEmpty()) {
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Your Cart is Empty")
                .setMessage("Add something to make me happy.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return
        }

        val count = CartManager.getItemCount()
        val total = CartManager.getTotalPrice()

        // Tạo chuỗi bill
        val billDetails = buildString {
            append("Your Order:\n\n")
            CartManager.cartItems.forEach {
                append("- ${it.product.name} x ${it.quantity} = ${formatCurrencyVN(it.product.price * it.quantity)}₫\n")
            }
            append("\nTotal items: $count")
            append("\nTotal price: ${formatCurrencyVN(total)}₫")
        }

        // Hiển thị AlertDialog
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Thank You For Your Order")
            .setMessage(billDetails)
            .setPositiveButton("OK") { dialog, _ ->
                // clear cart
                CartManager.clear()
                adapter.updateData(CartManager.cartItems.toMutableList())
                updateCartSummary()
                dialog.dismiss()
            }
            .show()
    }


    override fun onResume() {
        super.onResume()
        adapter.updateData(CartManager.cartItems.toMutableList())
        updateCartSummary()
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
