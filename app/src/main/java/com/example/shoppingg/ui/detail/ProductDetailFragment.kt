package com.example.shoppingg.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.shoppingg.R
import com.example.shoppingg.data.CartManager
import com.example.shoppingg.ui.models.Product
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class ProductDetailFragment : Fragment() {

    private var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = arguments?.getSerializable("product") as? Product
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_detail, container, false)

        val img = view.findViewById<ImageView>(R.id.product_image)
        val name = view.findViewById<TextView>(R.id.product_name)
        val desc = view.findViewById<TextView>(R.id.product_description)
        val price = view.findViewById<TextView>(R.id.product_price)
        val btnAddToCart = view.findViewById<Button>(R.id.btn_add_to_cart)

        product?.let { p ->
            name.text = p.name
            desc.text = p.description
            price.text = p.priceFormatted

            Glide.with(this)
                .load(p.image)  // p.image là URL
                .placeholder(R.drawable.ic_launcher_foreground) // ảnh tạm thời khi tải
                .error(R.drawable.ic_launcher_foreground)       // ảnh khi lỗi
                .into(img)
        }

        btnAddToCart.setOnClickListener {
            product?.let { p ->
                // thêm vào giỏ hàng
                CartManager.addItem(p)

                // hiển thị Snackbar với nút “Xem giỏ hàng”
                Snackbar.make(view, "Added to Your Cart", Snackbar.LENGTH_LONG)
                    .setAction("View Cart") {
                        // chuyển trang
                        findNavController().navigate(R.id.navigation_cart)

                        // cập nhật highlight menu
                        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
                        bottomNav.selectedItemId = R.id.navigation_cart
                    }.show()

            }
        }


        return view
    }
}
