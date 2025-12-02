package com.example.shoppingg.ui.checkout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingg.R

data class tempOrderItem(
    val name: String,
    val image: String,
    val quantity: Int
)

data class tempOrder(
    val orderId: String,
    val total: String,
    val totalItems: Int,
    val address: String,
    val phone: String,
    val items: List<tempOrderItem>
)

class CheckoutAdapter(private val orders: List<tempOrder>) :
    RecyclerView.Adapter<CheckoutAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvListOrder: LinearLayout = itemView.findViewById(R.id.tvListOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_order_in_checkout, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.tvOrderId.text = "Order ID: #${order.orderId}"

        val container = holder.tvListOrder
        container.removeAllViews()

        // Thêm từng item con
        for (item in order.items) {
            val itemView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_order_detail, container, false)

            val img = itemView.findViewById<ImageView>(R.id.tvOderImage)
            val tv = itemView.findViewById<TextView>(R.id.tvOrderDetails)

            Glide.with(holder.itemView.context)
                .load(item.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(img)

            tv.text = "${item.name} x${item.quantity}"

            container.addView(itemView)
        }
    }

    override fun getItemCount(): Int = orders.size
}
