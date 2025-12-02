package com.example.shoppingg.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
import androidx.appcompat.app.AlertDialog
import com.example.shoppingg.data.SessionManager
import com.example.shoppingg.ui.checkout.tempOrder
import com.example.shoppingg.ui.checkout.tempOrderItem
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.NumberParseException

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
            showCheckout()
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

    private fun showCheckout() {
        val items = CartManager.cartItems

        // Tạo order tạm thời để hiển thị ở Checkout
        val session = SessionManager(requireContext())
        val phone = session.getUserPhone() ?: ""
        val address = session.getUserAddress() ?: ""
        val total = CartManager.getTotalPrice()

        val temp = tempOrder(
            orderId = System.currentTimeMillis().toString(),
            total = formatCurrencyVN(total),
            totalItems = items.sumOf { it.quantity },
            address = address,
            phone = phone,
            items = items.map { cartItem ->
                tempOrderItem(
                    name = cartItem.product.name,
                    image = cartItem.product.image,
                    quantity = cartItem.quantity
                )
            }
        )

        // Truyền order tạm qua OrderManager tạm hoặc bundle (ở đây dùng OrderManager tạm)
        OrderManager.setTempOrder(temp)

        // Chuyển sang Checkout
        findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment)
    }


//    private fun showCheckoutInfoDialog() {
//        val dialogView = layoutInflater.inflate(R.layout.dialog_checkout, null)
//        val etPhone = dialogView.findViewById<EditText>(R.id.etPhone)
//        val etProvince = dialogView.findViewById<EditText>(R.id.etProvince)
//        val etDistrict = dialogView.findViewById<EditText>(R.id.etDistrict)
//        val etWard = dialogView.findViewById<EditText>(R.id.etWard)
//        val etStreet = dialogView.findViewById<EditText>(R.id.etStreet)
//
//        val phoneUtil = PhoneNumberUtil.getInstance()
//
//        etPhone.setOnFocusChangeListener { _, hasFocus ->
//            if (!hasFocus) {
//                val phone = etPhone.text.toString().trim()
//                try {
//                    val numberProto = phoneUtil.parse(phone, "VN")
//                    if (phoneUtil.isValidNumberForRegion(numberProto, "VN")) {
//                        etPhone.setText(phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL))
//                        etPhone.error = null
//                    } else etPhone.error = "Invalid Vietnam phone number"
//                } catch (e: NumberParseException) {
//                    etPhone.error = "Invalid phone number"
//                }
//            }
//        }
//
//        val alertDialog = AlertDialog.Builder(requireContext())
//            .setTitle("Add Shipping Address")
//            .setView(dialogView)
//            .setPositiveButton("Continue", null)
//            .setNegativeButton("Cancel", null)
//            .create()
//
//        alertDialog.setOnShowListener {
//            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
//                var isValid = true
//
//                fun validateNotEmpty(et: EditText, msg: String) {
//                    if (et.text.toString().trim().isEmpty()) {
//                        et.error = msg
//                        isValid = false
//                    } else et.error = null
//                }
//
//                validateNotEmpty(etPhone, "Phone number is required")
//                validateNotEmpty(etProvince, "City is required")
//                validateNotEmpty(etDistrict, "District is required")
//                validateNotEmpty(etWard, "Ward is required")
//                validateNotEmpty(etStreet, "Street is required")
//
//                val phone = etPhone.text.toString().trim()
//                if (!phone.isEmpty()) {
//                    try {
//                        val numberProto = phoneUtil.parse(phone, "VN")
//                        if (!phoneUtil.isValidNumberForRegion(numberProto, "VN")) {
//                            etPhone.error = "Invalid Vietnam phone number"
//                            isValid = false
//                        } else {
//                            etPhone.setText(phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL))
//                            etPhone.error = null
//                        }
//                    } catch (e: NumberParseException) {
//                        etPhone.error = "Invalid phone number"
//                        isValid = false
//                    }
//                }
//
//                if (!isValid) return@setOnClickListener
//
//                val address = "${etStreet.text}, ${etWard.text}, ${etDistrict.text}, ${etProvince.text}"
//                showBillAndClearCart(address, phone)
//                alertDialog.dismiss()
//            }
//        }
//        alertDialog.show()
//    }
//
//
//
//
//    private fun showBillAndClearCart(address: String, phone: String) {
//        val count = CartManager.getItemCount()
//        val total = CartManager.getTotalPrice()
//
//        val billDetails = buildString {
//            append("Address: $address")
//            append("\nPhone: $phone")
//
//            append("\n\nYour Order:\n\n")
//            CartManager.cartItems.forEach {
//                append("- ${it.product.name} x ${it.quantity} = ${formatCurrencyVN(it.product.price * it.quantity)}₫\n")
//            }
//            append("\nTotal items: $count")
//            append("\nTotal price: ${formatCurrencyVN(total)}₫")
//        }
//
//        AlertDialog.Builder(requireContext())
//            .setTitle("Thank You For Your Order")
//            .setMessage(billDetails)
//            .setPositiveButton("OK") { dialog, _ ->
//                Toast.makeText(requireContext(), "Checkout successful!", Toast.LENGTH_SHORT).show()
//
//                val items = CartManager.cartItems
//
//                val order = Order(
//                    orderId = System.currentTimeMillis().toString(),
//                    total = formatCurrencyVN(total),
//                    totalItems = items.sumOf { it.quantity },
//                    address = address,
//                    phone = phone,
//                    items = items.map {
//                        OrderItem(
//                            name = it.product.name,
//                            image = it.product.image,
//                            quantity = it.quantity
//                        )
//                    }
//                )
//
//                OrderManager.addOrder(order)
//
//                CartManager.clear()
//                updateCartSummary()
//                checkEmptyCart()
//                dialog.dismiss()
//            }
//            .show()
//    }



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