package com.example.shoppingg.ui.home

import CarouselAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.shoppingg.MainActivity
import com.example.shoppingg.R
import com.example.shoppingg.databinding.FragmentHomeBinding
import com.example.shoppingg.ui.adapter.Category
import com.example.shoppingg.ui.adapter.CategoryAdapter
import com.example.shoppingg.ui.adapter.ProductAdapter
import com.example.shoppingg.ui.models.Product
import android.os.Handler
import android.os.Looper
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.navigation.NavOptions


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ProductAdapter
    private var allProducts: List<Product> = emptyList()
    private lateinit var images: List<Int>
    private lateinit var viewPager: ViewPager2

    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private var currentCategory = "All"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewPager = binding.carouselViewPager

        images = listOf(
            R.drawable.carousel1,
            R.drawable.carousel2,
            R.drawable.carousel3
        )

        viewPager.adapter = CarouselAdapter(images)
        viewPager.offscreenPageLimit = 1

        autoSlideImages()

        // Suggestion product list
        binding.recyclerViewSuggestions.layoutManager =
            GridLayoutManager(requireContext(), 2)
        adapter = ProductAdapter(emptyList(), onClick = { product ->
            val bundle = Bundle().apply { putSerializable("product", product) }
            findNavController().navigate(R.id.productDetailFragment, bundle)
        }, isSuggestion = true)
        binding.recyclerViewSuggestions.adapter = adapter

        val categories = listOf(
            Category("All", R.drawable.ic_all),
            Category("Phone", R.drawable.ic_phone),
            Category("Laptop", R.drawable.ic_laptop),
            Category("Clock", R.drawable.ic_clock),
            Category("PC", R.drawable.ic_pc),
            Category("Electronic", R.drawable.ic_electronic)
        )

        val selectedCategory = arguments?.getString("selectedCategory") ?: "All"
        currentCategory = selectedCategory


        val categoryAdapter = CategoryAdapter(categories) { selectedCategory ->
            currentCategory = selectedCategory.name
            adapter.updateData(emptyList())
            viewModel.loadProducts(requireContext(), selectedCategory.name)
        }
        categoryAdapter.selectAll()

        // Categories
        binding.recyclerViewCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCategories.adapter = categoryAdapter

        // Loading
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvSeeAll.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.tvSeeAll.visibility = View.VISIBLE
            }
        }


        // Observe
        viewModel.products.observe(viewLifecycleOwner) { products ->
            allProducts = products
            adapter.updateData(allProducts.take(4))
        }

        // See all
        binding.tvSeeAll.setOnClickListener {
            val bundle = Bundle().apply {
                putString("selectedCategory", currentCategory)
            }
            findNavController().navigate(R.id.categoryFragment, bundle)
        }

        viewModel.loadProducts(requireContext(), currentCategory)

        val searchView = binding.layoutSearch.searchView

        searchView.setOnQueryTextListener(object : OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    val bundle = Bundle().apply {
                        putString("query", query)
                        putSerializable("products", ArrayList(allProducts))
                    }

                    searchView.setQuery("", false)
                    searchView.clearFocus()

                    findNavController().navigate(
                        R.id.searchFragment,
                        bundle,
                        NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setPopUpTo(R.id.navigation_home, false)
                            .build()
                    )

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun autoSlideImages() {
        val adapter = viewPager.adapter ?: return
        handler.postDelayed(object : Runnable {
            override fun run() {
                val itemCount = adapter.itemCount
                if (itemCount == 0) return

                currentPage = (currentPage + 1) % itemCount
                viewPager.setCurrentItem(currentPage, true)

                handler.postDelayed(this, 3000)
            }
        }, 3000)
    }
}