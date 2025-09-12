package com.example.shoppingg.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingg.R
import com.example.shoppingg.databinding.FragmentHomeBinding
import com.example.shoppingg.ui.adapter.ProductAdapter
import com.example.shoppingg.ui.models.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var allProducts: List<Product>
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Load data từ JSON
        allProducts = loadProductsFromJson()

        // Setup RecyclerView
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProductAdapter(allProducts) { product ->
            val bundle = Bundle().apply { putSerializable("product", product) }
            findNavController().navigate(R.id.productDetailFragment, bundle)
        }
        binding.recyclerViewProducts.adapter = adapter

        // Filter theo category
        binding.radioGroupFilter.setOnCheckedChangeListener { _, checkedId ->
            val filteredList = when (checkedId) {
                R.id.rbPhone -> allProducts.filter { it.category == "Phone" }
                R.id.rbLaptop -> allProducts.filter { it.category == "Laptop" }
                R.id.rbClock -> allProducts.filter { it.category == "Clock" }
                R.id.rbPC -> allProducts.filter { it.category == "PC" }
                R.id.rbElectronic -> allProducts.filter { it.category == "Electronic" }
                else -> allProducts
            }
            adapter.updateData(filteredList)
        }

        return binding.root
    }

    private fun loadProductsFromJson(): List<Product> {
        return try {
            val inputStream = requireContext().assets.open("products.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Product>>() {}.type
            Gson().fromJson(json, listType)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
