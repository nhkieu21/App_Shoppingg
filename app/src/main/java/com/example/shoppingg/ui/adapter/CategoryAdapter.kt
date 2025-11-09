package com.example.shoppingg.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingg.R

class CategoryAdapter(
    private val categories: List<Category>,
    private val onClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition: Int? = null

    fun selectCategory(categoryName: String) {
        val index = categories.indexOfFirst { it.name == categoryName }
        if (index != -1) {
            selectedPosition = index
            notifyDataSetChanged()
        }
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imageCategory)
        val name: TextView = itemView.findViewById(R.id.tvCategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.name.text = category.name
        holder.image.setImageResource(category.imageRes)

        val backgroundDrawable = holder.image.background
        if (selectedPosition == position) {
            backgroundDrawable.setTint(Color.parseColor("#BDBDBD"))
        } else {
            backgroundDrawable.setTint(Color.parseColor("#E0E0E0"))
        }

        holder.itemView.setOnClickListener {
            val currentPos = holder.adapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                val previousPosition = selectedPosition
                selectedPosition = currentPos

                previousPosition?.let { notifyItemChanged(it) }
                notifyItemChanged(currentPos)

                onClick(categories[currentPos])
            }
        }
    }

    override fun getItemCount() = categories.size

    fun selectAll() {
        val previousPosition = selectedPosition
        selectedPosition = 0
        previousPosition?.let { notifyItemChanged(it) }
        notifyItemChanged(0)
    }
}

data class Category(val name: String, val imageRes: Int)
