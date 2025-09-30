package com.example.shoppingg.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingg.MainActivity
import com.example.shoppingg.R
import com.example.shoppingg.databinding.FragmentHomeBinding
import com.example.shoppingg.ui.adapter.ProductAdapter
import com.example.shoppingg.ui.models.Product

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ProductAdapter
    private var allProducts: List<Product> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // RecyclerView setup
        binding.recyclerViewProducts.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProductAdapter(emptyList()) { product ->
            val bundle = Bundle().apply { putSerializable("product", product) }
            findNavController().navigate(R.id.productDetailFragment, bundle)
        }
        binding.recyclerViewProducts.adapter = adapter

        // Quan sát loading
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                (activity as? MainActivity)?.showLoading()
            } else {
                (activity as? MainActivity)?.hideLoading()
            }
        }

        // Quan sát dữ liệu
        viewModel.products.observe(viewLifecycleOwner) { products ->
            allProducts = products
            adapter.updateData(products)
        }

        // Gọi load dữ liệu
        viewModel.loadProducts(requireContext())

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
