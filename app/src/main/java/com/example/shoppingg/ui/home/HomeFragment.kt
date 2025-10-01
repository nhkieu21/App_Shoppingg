package com.example.shoppingg.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

        // Loading
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                (activity as? MainActivity)?.showLoading()
            } else {
                (activity as? MainActivity)?.hideLoading()
            }
        }

        viewModel.products.observe(viewLifecycleOwner) { products ->
            allProducts = products
            adapter.updateData(products)
        }

        viewModel.loadProducts(requireContext())

        // Dropdown
        val categories = listOf("All", "Phone", "Laptop", "Clock", "PC", "Electronic")
        val spinnerAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilter.adapter = spinnerAdapter

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = categories[position]
                val filteredList = if (selected == "All") {
                    allProducts
                } else {
                    allProducts.filter { it.category == selected }
                }
                adapter.updateData(filteredList)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
