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
import com.example.shoppingg.R
import com.example.shoppingg.databinding.FragmentCategoryBinding
import com.example.shoppingg.ui.adapter.Category
import com.example.shoppingg.ui.adapter.CategoryAdapter
import com.example.shoppingg.ui.adapter.ProductAdapter
import com.example.shoppingg.ui.home.HomeViewModel
import com.example.shoppingg.ui.models.Product
import kotlin.math.ceil

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ProductAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private var currentCategory = "All"
    private val pageSize = 6

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        currentCategory = viewModel.savedCategory

        if (viewModel.products.value.isNullOrEmpty()) {
            viewModel.loadProducts(requireContext(), currentCategory)
        }

        setupRecyclerViews()
        setupPaginationButtons()
        observeViewModel()

        return binding.root
    }

    private fun setupRecyclerViews() {
        // Product Grid
        binding.recyclerViewAllProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ProductAdapter(emptyList(), onClick = { product ->
            val bundle = Bundle().apply { putSerializable("product", product) }
            findNavController().navigate(R.id.productDetailFragment, bundle)
        }, isSuggestion = true)
        binding.recyclerViewAllProducts.adapter = adapter

        // Category List
        val categories = listOf(
            Category("All", R.drawable.ic_all),
            Category("Phone", R.drawable.ic_phone),
            Category("Laptop", R.drawable.ic_laptop),
            Category("Clock", R.drawable.ic_clock),
            Category("PC", R.drawable.ic_pc),
            Category("Electronic", R.drawable.ic_electronic)
        )

        categoryAdapter = CategoryAdapter(categories) { selected ->
            if (currentCategory != selected.name) {
                currentCategory = selected.name
                viewModel.savedPage = 1
                viewModel.loadProducts(requireContext(), selected.name)
                categoryAdapter.selectCategory(selected.name)
            }
        }
        binding.recyclerViewCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategories.adapter = categoryAdapter
        categoryAdapter.selectCategory(currentCategory)
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE

            if (loading) {
                binding.layoutPagination.visibility = View.GONE
            }
        }

        viewModel.products.observe(viewLifecycleOwner) {
            updatePage()
        }
    }

    private fun setupPaginationButtons() {
        binding.btnPrev.setOnClickListener {
            if (viewModel.savedPage > 1) {
                viewModel.savedPage--
                updatePage()
            }
        }

        binding.btnNext.setOnClickListener {
            val totalPages = viewModel.getTotalPages(pageSize)
            if (viewModel.savedPage < totalPages) {
                viewModel.savedPage++
                updatePage()
            }
        }
    }

    private fun updatePage() {
        binding.recyclerViewAllProducts.scrollToPosition(0)
        viewModel.loadPage(viewModel.savedPage, pageSize) { pageData ->
            adapter.updateData(pageData)

            if (pageData.isNotEmpty()) {
                val totalPages = viewModel.getTotalPages(pageSize)
                binding.tvPageNumber.text = "${viewModel.savedPage} / $totalPages"
                binding.btnPrev.isEnabled = viewModel.savedPage > 1
                binding.btnNext.isEnabled = viewModel.savedPage < totalPages

                binding.layoutPagination.visibility =
                    if (totalPages > 1) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}