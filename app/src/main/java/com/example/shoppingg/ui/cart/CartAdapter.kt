package com.example.shoppingg.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingg.R
import com.example.shoppingg.data.CartManager
import com.example.shoppingg.ui.models.CartItem


class CartAdapter(private var items:  MutableList<CartItem>,
                  private val onCartChanged: () -> Unit ) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.product_name)
        val price: TextView = itemView.findViewById(R.id.product_price)
        val image: ImageView = itemView.findViewById(R.id.product_image)
        val qty: TextView = itemView.findViewById(R.id.product_quantity)
        val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.product.name
        holder.price.text = item.product.priceFormatted
        holder.qty.text = "Quantity: ${item.quantity}"

        Glide.with(holder.itemView.context)
            .load(item.product.image)  // item.product.image là URL hoặc drawable
            .placeholder(R.drawable.ic_launcher_foreground) // ảnh tạm thời khi tải
            .error(R.drawable.ic_launcher_foreground)      // ảnh khi lỗi
            .into(holder.image)

        // nút xóa
        holder.btnDelete.setOnClickListener {
            removeItem(holder.adapterPosition)

        }
    }

    override fun getItemCount() = items.size

    fun removeItem(position: Int) {
        if (position in items.indices) {
            val item = items[position]
            // xóa trong CartManager
            CartManager.removeItem(item.product)  // hàm này bạn phải viết trong CartManager
            // xóa trong adapter
            items.removeAt(position)
            notifyItemRemoved(position)
            // báo về Fragment cập nhật tổng tiền
            onCartChanged()
        }
    }
    fun updateData(newItems: MutableList<CartItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

