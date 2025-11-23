package com.example.shoppingg.ui.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.shoppingg.R
import com.example.shoppingg.data.CartManager
import com.example.shoppingg.data.SessionManager
import com.example.shoppingg.ui.models.Product
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class ProductDetailFragment : Fragment() {

    private var product: Product? = null
    private var quantity = 1

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
        val btnMinus = view.findViewById<Button>(R.id.btn_minus)
        val btnPlus = view.findViewById<Button>(R.id.btn_plus)
        val tvQuantity = view.findViewById<TextView>(R.id.tv_quantity)

        product?.let { p ->
            name.text = p.name
            desc.text = p.description
            price.text = p.priceFormatted

            Glide.with(this)
                .load(p.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(img)
        }

        btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQuantity.text = quantity.toString()
            }
        }

        btnPlus.setOnClickListener {
            quantity++
            tvQuantity.text = quantity.toString()
        }



        btnAddToCart.setOnClickListener {
            val sessionManager = SessionManager(view.context)
            val isLoggedIn = sessionManager.isLoggedIn()

            if (!isLoggedIn) {
                AlertDialog.Builder(view.context)
                    .setTitle("Login Required")
                    .setMessage("You must be logged in to add to cart")
                    .setPositiveButton("Login") { _, _ ->

                        val activity = view.context as FragmentActivity
                        val navController = activity.findNavController(R.id.nav_host_fragment_activity_main)
                        navController.navigate(R.id.navigation_account)

                        val bottomNav = activity.findViewById<BottomNavigationView>(R.id.nav_view)
                        bottomNav?.selectedItemId = R.id.navigation_account
                    }
                    .setNegativeButton("Not now", null)
                    .show()

                return@setOnClickListener
            }

            product?.let { p ->
                repeat(quantity) {
                    CartManager.addItem(p)
                }
                val itemText = if (quantity == 1) "item" else "items"

                Snackbar.make(view, "Added $quantity $itemText to your cart", Snackbar.LENGTH_LONG)
                    .setAction("View Cart") {
                        findNavController().popBackStack(R.id.navigation_home, false)
                        val bottomNav =
                            (view.context as? androidx.fragment.app.FragmentActivity)
                                ?.findViewById<BottomNavigationView>(R.id.nav_view)
                        bottomNav?.selectedItemId = R.id.navigation_cart
                    }.show()
                quantity = 1
                tvQuantity.text = quantity.toString()
            }
        }


        return view
    }
}
