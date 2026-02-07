package com.example.shoppingg.ui.home

import kotlinx.coroutines.delay
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingg.ui.models.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class HomeViewModel : ViewModel() {
    var savedPage: Int = 1
    var savedCategory: String = "All"
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    fun loadProducts(context: Context, category: String) {
        savedCategory = category
        _isLoading.value = true
        _products.value = emptyList()
        viewModelScope.launch {
            delay(300)
            try {
                val productList = withContext(Dispatchers.IO) {
                    val inputStream = context.assets.open("products.json")
                    val json = inputStream.bufferedReader().use { it.readText() }
                    val listType = object : TypeToken<List<Product>>() {}.type
                    Gson().fromJson<List<Product>>(json, listType)
                }
                _products.value = if (category == "All") productList
                else productList.filter { it.category == category }

            } catch (e: IOException) {
                e.printStackTrace()
                _products.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun loadPage(currentPage: Int, pageSize: Int, onComplete: (List<Product>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            onComplete(emptyList())
            delay(300)
            val allData = _products.value ?: emptyList()
            val startIndex = (currentPage - 1) * pageSize
            val pageData = allData.drop(startIndex).take(pageSize)

            onComplete(pageData)
            _isLoading.value = false
        }
    }

    fun getTotalPages(pageSize: Int): Int {
        val total = _products.value?.size ?: 0
        return if (total == 0) 1 else Math.ceil(total.toDouble() / pageSize).toInt()
    }
}
