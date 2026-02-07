package com.example.shoppingg.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingg.ui.models.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

class SearchViewModel : ViewModel() {

    val products = MutableLiveData<List<Product>>()
    val isLoading = MutableLiveData(false)

    var allProducts: List<Product> = emptyList()
    var filteredProducts: List<Product> = emptyList()

    var savedQuery = ""
    var savedPage = 1

    fun setProducts(list: List<Product>) {
        allProducts = list
    }

    fun search(query: String) {
        savedQuery = query
        savedPage = 1

        isLoading.value = true

        val result = allProducts.filter {
            it.name.lowercase().contains(query.lowercase())
        }

        products.value = result
        isLoading.value = false
    }

    fun getTotalPages(pageSize: Int): Int {
        val total = products.value?.size ?: 0
        return if (total == 0) 1 else Math.ceil(total.toDouble() / pageSize).toInt()
    }
    fun loadPage(currentPage: Int, pageSize: Int, onComplete: (List<Product>) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            onComplete(emptyList())
            delay(300)
            val allData = products.value ?: emptyList()
            val startIndex = (currentPage - 1) * pageSize
            val pageData = allData.drop(startIndex).take(pageSize)

            onComplete(pageData)
            isLoading.value = false
        }
    }

}
