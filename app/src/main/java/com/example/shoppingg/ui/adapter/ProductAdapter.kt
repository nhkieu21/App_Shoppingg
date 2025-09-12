package com.example.shoppingg.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingg.R
import com.example.shoppingg.ui.models.Product

class ProductAdapter(
    private var products: List<Product>, // ← đổi thành var
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val productName: TextView = view.findViewById(R.id.product_name)
        private val productPrice: TextView = view.findViewById(R.id.product_price)
        private val productImage: ImageView = view.findViewById(R.id.product_image)
        private var currentProduct: Product? = null

        init {
            view.setOnClickListener {
                currentProduct?.let(onClick)
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
        }

    }

    // cho phép thay đổi danh sách và reload
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
