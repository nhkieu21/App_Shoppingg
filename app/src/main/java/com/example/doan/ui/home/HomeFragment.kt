// HomeFragment.kt
package com.example.doan

import com.example.doan.ui.models.Product
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ProductAdapter(getProducts()) { product ->
            // Xử lý sự kiện khi bấm vào sản phẩm
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra("PRODUCT", product)
            }
            startActivity(intent)
        }
        return view
    }

    private fun getProducts(): List<Product> {
        // Tạo danh sách sản phẩm mẫu
        return listOf(
            Product("iPhone", "Điện thoại", 1000.0, "Mô tả sản phẩm...", R.drawable.iphone),
            Product("Laptop", "Máy tính", 1500.0, "Mô tả sản phẩm...", R.drawable.laptop),
            // Thêm các sản phẩm khác
        )
    }
}
