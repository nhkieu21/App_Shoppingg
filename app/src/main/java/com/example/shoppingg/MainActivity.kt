package com.example.shoppingg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.shoppingg.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Các fragment gốc (không hiển thị nút back)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_cart
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Pop về fragment gốc của Home
                    navController.popBackStack(R.id.navigation_home, false)
                    // Điều hướng tới Home nếu chưa ở Home
                    if (navController.currentDestination?.id != R.id.navigation_home) {
                        navController.navigate(R.id.navigation_home)
                    }
                    true
                }
                R.id.navigation_cart -> {
                    // Pop về fragment gốc của Cart
                    navController.popBackStack(R.id.navigation_cart, false)
                    if (navController.currentDestination?.id != R.id.navigation_cart) {
                        navController.navigate(R.id.navigation_cart)
                    }
                    true
                }
                else -> false
            }
        }

//        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
