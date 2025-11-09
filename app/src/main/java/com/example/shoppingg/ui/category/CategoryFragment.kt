package com.example.shoppingg.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingg.MainActivity
import com.example.shoppingg.R
import com.example.shoppingg.databinding.FragmentCategoryBinding
import com.example.shoppingg.ui.adapter.Category
import com.example.shoppingg.ui.adapter.CategoryAdapter
import com.example.shoppingg.ui.adapter.ProductAdapter
import com.example.shoppingg.ui.home.HomeViewModel
import com.example.shoppingg.ui.models.Product

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter
    private var currentCategory = "All"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        currentCategory = arguments?.getString("selectedCategory") ?: "All"

        binding.recyclerViewAllProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ProductAdapter(emptyList(), onClick = { product ->
            val bundle = Bundle().apply { putSerializable("product", product) }
            findNavController().navigate(R.id.productDetailFragment, bundle)
        }, isSuggestion = true)
        binding.recyclerViewAllProducts.adapter = adapter

        val categories = listOf(
            Category("All", R.drawable.ic_all),
            Category("Phone", R.drawable.ic_phone),
            Category("Laptop", R.drawable.ic_laptop),
            Category("Clock", R.drawable.ic_clock),
            Category("PC", R.drawable.ic_pc),
            Category("Electronic", R.drawable.ic_electronic)
        )

        categoryAdapter = CategoryAdapter(categories) { selected ->
            currentCategory = selected.name
            adapter.updateData(emptyList())
            viewModel.loadProducts(requireContext(), selected.name)
        }

        binding.recyclerViewCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategories.adapter = categoryAdapter

        categoryAdapter.selectCategory(currentCategory)

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.updateData(products)
        }

        viewModel.loadProducts(requireContext(), currentCategory)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
