package com.example.shoppingg.ui.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingg.R
import com.example.shoppingg.data.CartManager
import com.example.shoppingg.data.OrderManager
import com.example.shoppingg.data.SessionManager
import com.example.shoppingg.databinding.FragmentCheckoutBinding
import com.example.shoppingg.ui.orders.Order
import com.example.shoppingg.ui.orders.OrderItem
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.text.NumberFormat
import java.util.Locale

class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private var payment: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)

        loadUserData()
        loadCartSummary()
        setupPaymentMethodUI()
        setupClickListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tempOrder = OrderManager.getTempOrder()

        tempOrder?.let {
            val ordersAdapter = CheckoutAdapter(listOf(it))
            binding.rvTempOrders.layoutManager = LinearLayoutManager(requireContext())
            binding.rvTempOrders.adapter = ordersAdapter
        }
    }


    private fun setupClickListeners() {
        binding.btnMakePayment.setOnClickListener { goToPaymentSuccess() }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    /** Load thông tin người dùng */
    private fun loadUserData() {
        val session = SessionManager(requireContext())
        updateShippingInfo(session.getUserPhone(), session.getUserAddress())
    }

    private fun updateShippingInfo(phone: String?, address: String?) {
        binding.etPhone.setText(phone ?: "")
        binding.etPhone.hint = if (phone.isNullOrEmpty()) "Enter phone number" else ""

        binding.etAddress.setText(address ?: "")
        binding.etAddress.hint = if (address.isNullOrEmpty()) "Enter address" else ""
    }

    /** Load tổng đơn hàng */
    private fun loadCartSummary() {
        val count = CartManager.getItemCount()
        val total = CartManager.getTotalPrice().toLong()
        binding.tvCartSummary.text = "Items: $count | Total: ${formatCurrencyVN(total)}"
    }

    /** Chọn phương thức thanh toán */
    private fun setupPaymentMethodUI() {
        binding.payCashOnDelivery.setOnClickListener { selectPayment("Cash On Delivery") }
        binding.payPaypal.setOnClickListener { selectPayment("Paypal") }
    }

    private fun selectPayment(method: String) {
        binding.payCashOnDelivery.setBackgroundResource(R.drawable.bg_button)
        binding.payPaypal.setBackgroundResource(R.drawable.bg_button)

        when (method) {
            "Cash On Delivery" ->
                binding.payCashOnDelivery.setBackgroundResource(R.drawable.payment_selected_bg)
            "Paypal" ->
                binding.payPaypal.setBackgroundResource(R.drawable.payment_selected_bg)
        }

        payment = method
    }

    /** Điều hướng sang Payment Success */
    private fun goToPaymentSuccess() {
        val inputPhone = binding.etPhone.text.toString().trim()
        val inputAddress = binding.etAddress.text.toString().trim()

        // 1. Kiểm tra trường rỗng
        if (inputPhone.isEmpty()) {
            showToast("Please enter phone number")
            binding.etPhone.requestFocus()
            return
        }
        val formattedPhone: String
        try {
            val phoneUtil = PhoneNumberUtil.getInstance()
            val numberProto = phoneUtil.parse(inputPhone, "VN")

            // Tạm thời bỏ qua kiểm tra isValidNumberForRegion để linh hoạt hơn, chỉ cần kiểm tra parse được.
            // Nếu cần kiểm tra nghiêm ngặt:
            if (!phoneUtil.isValidNumberForRegion(numberProto, "VN")) {
                showToast("Invalid Vietnam phone number")
                binding.etPhone.requestFocus()
                return
            }

            formattedPhone = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
            // Cập nhật lại EditText với định dạng chuẩn (tùy chọn)
            binding.etPhone.setText(formattedPhone)

        } catch (e: NumberParseException) {
            showToast("Invalid phone number format")
            binding.etPhone.requestFocus()
            return
        }

        if (inputAddress.isEmpty()) {
            showToast("Please enter address")
            binding.etAddress.requestFocus()
            return
        }
        if (payment.isEmpty()) {
            showToast("Please select payment method")
            return
        }

        // 2. Validate số điện thoại


        // 3. Lưu thông tin mới vào SessionManager
        SessionManager(requireContext()).saveUserPhone(formattedPhone)
        SessionManager(requireContext()).saveUserAddress(inputAddress)

        // 4. Cập nhật OrderManager và thực hiện thanh toán
        OrderManager.getTempOrder()?.let { temp ->
            // Cập nhật thông tin Shipping mới nhất cho Order
            val finalOrder = Order(
                orderId = temp.orderId,
                total = temp.total,
                totalItems = temp.totalItems,
                // Gán số điện thoại và địa chỉ từ EditText (đã được lưu vào SessionManager ở trên)
                address = inputAddress,
                phone = formattedPhone,
                items = temp.items.map {
                    OrderItem(
                        it.name,
                        it.image,
                        it.quantity)
                }
            )
            OrderManager.addOrder(finalOrder)
        }


        // 5. Kết thúc giao dịch
        CartManager.clear()
        OrderManager.clearTempOrder()

        Toast.makeText(requireContext(), "Checkout successful!", Toast.LENGTH_SHORT).show()

        // Điều hướng về Home (hoặc màn hình Order Success)
        findNavController().navigate(R.id.navigation_home)
        // Xóa checkoutFragment khỏi back stack
        findNavController().popBackStack(R.id.checkoutFragment, true)
    }


    private fun formatCurrencyVN(amount: Long): String =
        NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)

    private fun showToast(message: String) =
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
