package com.example.shoppingg.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppingg.R
import com.example.shoppingg.databinding.FragmentSearchBinding
import com.example.shoppingg.ui.adapter.ProductAdapter
import com.example.shoppingg.ui.models.Product
import kotlin.math.ceil

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductAdapter
    private val viewModel: SearchViewModel by viewModels()
    private val pageSize = 6
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupPaginationButtons()

        // Lấy dữ liệu sản phẩm từ arguments nếu ViewModel chưa có
        if (viewModel.allProducts.isEmpty()) {
            viewModel.allProducts = arguments?.getSerializable("products") as? List<Product> ?: emptyList()
        }

        setupSearchView()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewSuggestions.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ProductAdapter(
            emptyList(),
            onClick = { product ->
                val bundle = Bundle().apply { putSerializable("product", product) }
                findNavController().navigate(R.id.productDetailFragment, bundle)
            },
            isSuggestion = true
        )
        binding.recyclerViewSuggestions.adapter = adapter
    }

    private fun setupSearchView() {
        val searchView = binding.layoutSearch.searchView
        searchView.isIconified = false

        val initialQuery = if (viewModel.savedQuery.isNotBlank()) {
            viewModel.savedQuery
        } else {
            arguments?.getString("query") ?: ""
        }

        if (initialQuery.isNotBlank()) {
            searchView.setQuery(initialQuery, false)
            searchView.clearFocus()
            filterProducts(initialQuery, isRestoring = true)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterProducts(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    hideAll()
                }
                return true
            }
        })
    }

    private fun filterProducts(query: String?, isRestoring: Boolean = false) {
        if (query.isNullOrBlank()) {
            hideAll()
            return
        }

        // Cập nhật query và reset trang
        if (viewModel.savedQuery != query && !isRestoring) {
            viewModel.savedPage = 1
        }
        viewModel.savedQuery = query

        // Thực hiện lọc ngay lập tức
        val keys = query.lowercase().trim().split("\\s+".toRegex())
        viewModel.filteredProducts = viewModel.allProducts.filter { product ->
            val name = product.name.lowercase()
            val cate = product.category?.lowercase() ?: ""
            keys.any { k -> name.contains(k) || cate.contains(k) }
        }

        // Cập nhật giao diện
        if (viewModel.filteredProducts.isEmpty()) {
            binding.recyclerViewSuggestions.visibility = View.GONE
            binding.layoutNoResults.visibility = View.VISIBLE
            binding.layoutPagination.visibility = View.GONE
        } else {
            binding.layoutNoResults.visibility = View.GONE
            binding.recyclerViewSuggestions.visibility = View.VISIBLE
            updatePage() // Gọi hàm hiển thị dữ liệu lên trang
        }
    }

    private fun setupPaginationButtons() {
        binding.btnPrev.setOnClickListener {
            if (!isLoading && viewModel.savedPage > 1) {
                viewModel.savedPage--
                updatePage()
            }
        }

        binding.btnNext.setOnClickListener {
            val totalPages = getTotalPages()
            if (!isLoading && viewModel.savedPage < totalPages) {
                viewModel.savedPage++
                updatePage()
            }
        }
    }

    private fun updatePage() {
        showLoading(true)
        binding.recyclerViewSuggestions.scrollToPosition(0)

        binding.recyclerViewSuggestions.postDelayed({
            val startIndex = (viewModel.savedPage - 1) * pageSize
            val pageData = viewModel.filteredProducts.drop(startIndex).take(pageSize)

            adapter.updateData(pageData)

            val totalPages = getTotalPages()
            binding.tvPageNumber.text = "${viewModel.savedPage} / $totalPages"
            binding.btnPrev.isEnabled = viewModel.savedPage > 1
            binding.btnNext.isEnabled = viewModel.savedPage < totalPages

            binding.layoutPagination.visibility = if (totalPages > 1) View.VISIBLE else View.GONE
            showLoading(false)
        }, 300)
    }

    private fun getTotalPages(): Int {
        return if (viewModel.filteredProducts.isEmpty()) 1
        else ceil(viewModel.filteredProducts.size.toDouble() / pageSize).toInt()
    }

    private fun showLoading(show: Boolean) {
        isLoading = show
        binding.recyclerViewSuggestions.visibility = if (show) View.GONE else View.VISIBLE
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE

        if (show) {
            binding.layoutPagination.visibility = View.GONE
        }
    }

    private fun hideAll() {
        viewModel.savedPage = 1
        viewModel.filteredProducts = emptyList()
        viewModel.savedQuery = ""
        showLoading(false)
        binding.recyclerViewSuggestions.visibility = View.GONE
        binding.layoutNoResults.visibility = View.GONE
        binding.layoutPagination.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}