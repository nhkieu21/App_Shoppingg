package com.example.shoppingg.ui.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shoppingg.R

data class OrderItem(
    val name: String,
    val image: String,
    val quantity: Int
)
data class Order(
    val orderId: String,
    val total: String,
    val totalItems: Int,
    val items: List<OrderItem>
)

class MyOrdersAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<MyOrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvOrderTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)
        val tvOrderDetails: LinearLayout = itemView.findViewById(R.id.tvListOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderId.text = "Order ID: #${order.orderId}"
        holder.tvOrderTotal.text = "Total (${order.totalItems} items): ${order.total}đ"

        val tvListOrder = holder.tvOrderDetails
        tvListOrder.removeAllViews()

        for (item in order.items) {
            val itemView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_order_detail, tvListOrder, false)

            val imageView = itemView.findViewById<ImageView>(R.id.tvOderImage)
            val tvDetails = itemView.findViewById<TextView>(R.id.tvOrderDetails)

            Glide.with(holder.itemView.context)
                .load(item.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imageView)

            tvDetails.text = "${item.name} x${item.quantity}"
            tvListOrder.addView(itemView)
        }

    }

    override fun getItemCount(): Int = orders.size
}
