package com.example.shoppingg.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingg.R
import com.example.shoppingg.data.CartManager
import com.example.shoppingg.ui.models.Product
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class ProductAdapter(
    private var products: List<Product>,
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val productName: TextView = view.findViewById(R.id.product_name)
        private val productPrice: TextView = view.findViewById(R.id.product_price)
        private val productImage: ImageView = view.findViewById(R.id.product_image)

        private val btnAddToCart = view.findViewById<Button>(R.id.btn_add_to_cart)
        private val btnMinus = view.findViewById<Button>(R.id.btn_minus)
        private val btnPlus = view.findViewById<Button>(R.id.btn_plus)
        private val tvQuantity = view.findViewById<TextView>(R.id.tv_quantity)

        private var quantity = 1
        private var currentProduct: Product? = null

        init {
            view.setOnClickListener {
                currentProduct?.let(onClick)
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

            btnAddToCart.setOnClickListener { view ->
                currentProduct?.let { product ->
                    repeat(quantity) {
                        CartManager.addItem(product)
                    }
                    val itemText = if (quantity == 1) "item" else "items"

                    Snackbar.make(view, "Added $quantity $itemText to your cart", Snackbar.LENGTH_LONG)
                        .setAction("View Cart") {
                            val navController = (view.context as? androidx.fragment.app.FragmentActivity)
                                ?.supportFragmentManager
                                ?.primaryNavigationFragment
                                ?.findNavController()
                            navController?.navigate(R.id.navigation_cart)

                            val bottomNav =
                                (view.context as? androidx.fragment.app.FragmentActivity)
                                    ?.findViewById<BottomNavigationView>(R.id.nav_view)
                            bottomNav?.selectedItemId = R.id.navigation_cart
                        }.show()
                    quantity = 1
                    tvQuantity.text = quantity.toString()
                }
            }
        }

        fun bind(product: Product) {
            currentProduct = product
            productName.text = product.name
            productPrice.text = product.priceFormatted

            Glide.with(itemView)
                .load(product.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(productImage)

            quantity = 1
            tvQuantity.text = quantity.toString()
        }

    }

    fun updateData(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size
}
