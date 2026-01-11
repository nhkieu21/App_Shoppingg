package com.example.shoppingg.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppingg.R
import com.example.shoppingg.databinding.FragmentSearchBinding
import com.example.shoppingg.ui.adapter.ProductAdapter
import com.example.shoppingg.ui.models.Product

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductAdapter
    private var allProducts: List<Product> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        // RecyclerView
        binding.recyclerViewSuggestions.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ProductAdapter(emptyList(), onClick = { product ->
            val bundle = Bundle().apply { putSerializable("product", product) }
            findNavController().navigate(R.id.productDetailFragment, bundle)
        }, isSuggestion = true)
        binding.recyclerViewSuggestions.adapter = adapter

        allProducts = arguments?.getSerializable("products") as? List<Product> ?: emptyList()

        val searchView = binding.layoutSearch.searchView
        searchView.isIconified = false

        // Lấy keyword từ HomeFragment gửi sang
        val keyword = arguments?.getString("query") ?: ""
        if (keyword.isNotEmpty()) {
            searchView.setQuery(keyword, false)
            searchView.clearFocus()
            filterProducts(keyword)
        } else {
            // Chưa search gì → ẩn list & empty
            binding.recyclerViewSuggestions.visibility = View.GONE
            binding.layoutNoResults.visibility = View.GONE
        }

        // Search Listener
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProducts(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Nếu user xóa hết text → ẩn kết quả
                if (newText.isNullOrBlank()) {
                    filterProducts("")
                }
                return true
            }
        })

        return binding.root
    }

    private fun filterProducts(query: String?) {
        if (query.isNullOrBlank()) {
            binding.recyclerViewSuggestions.visibility = View.GONE
            binding.layoutNoResults.visibility = View.GONE
            return
        }

        val keys = query.lowercase().trim().split("\\s+".toRegex())

        val filteredList = allProducts.filter { product ->
            val name = product.name.lowercase()
            val cate = product.category?.lowercase() ?: ""
            keys.any { k -> name.contains(k) || cate.contains(k) }
        }

        if (filteredList.isEmpty()) {
            binding.recyclerViewSuggestions.visibility = View.GONE
            binding.layoutNoResults.visibility = View.VISIBLE
        } else {
            binding.layoutNoResults.visibility = View.GONE
            binding.recyclerViewSuggestions.visibility = View.VISIBLE
            adapter.updateData(filteredList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
