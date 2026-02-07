package com.example.shoppingg.ui.cart

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingg.R
import com.example.shoppingg.data.CartManager
import com.example.shoppingg.data.CartManager.removeItem
import com.example.shoppingg.ui.models.CartItem


class CartAdapter(private var items:  MutableList<CartItem>,
                  private val onCartChanged: () -> Unit ) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.product_name)
        val price: TextView = itemView.findViewById(R.id.product_price)
        val image: ImageView = itemView.findViewById(R.id.product_image)

        val btnMinus : Button = itemView.findViewById(R.id.btn_minus)

        val btnPlus : Button = itemView.findViewById(R.id.btn_plus)

        val qty: TextView = itemView.findViewById(R.id.product_quantity)

        val btnUpdate : Button = itemView.findViewById(R.id.btn_update_cart)

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
        holder.qty.text = item.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(item.product.image)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.image)

        holder.btnUpdate.isEnabled = false

        holder.btnMinus.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                holder.qty.text = item.quantity.toString()
                holder.btnUpdate.isEnabled = true
                holder.btnUpdate.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.red))
            } else {
                AlertDialog.Builder(holder.itemView.context)
                    .setMessage("Quantity must be greater than 0")
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

        holder.btnPlus.setOnClickListener {
            item.quantity++
            holder.qty.text = item.quantity.toString()
            holder.btnUpdate.isEnabled = true
            holder.btnUpdate.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }

        holder.btnUpdate.setOnClickListener {
            CartManager.updateQuantity(item.product, item.quantity)
            holder.btnUpdate.isEnabled = false
            onCartChanged()
            holder.btnUpdate.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.context, R.color.purple_700))
        }

        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Confirm delete")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete") { dialog, _ ->
                    removeItem(holder.adapterPosition)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

    }

    override fun getItemCount() = items.size

    fun removeItem(position: Int) {
        if (position in items.indices) {
            val item = items[position]

            CartManager.removeItem(item.product)

            items.removeAt(position)
            notifyItemRemoved(position)

            onCartChanged()
        }
    }
    fun updateData(newItems: MutableList<CartItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

