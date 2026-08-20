package com.example.shoppingg

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.shoppingg.data.SessionManager
import com.example.shoppingg.databinding.ActivityMainBinding
import com.example.shoppingg.ui.home.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private val handler = Handler(Looper.getMainLooper())

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navView: BottomNavigationView = binding.navView

        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.searchFragment,
                R.id.categoryFragment,
                R.id.navigation_cart,
                R.id.navigation_account,
                R.id.myOrdersFragment,
                R.id.loginFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { item ->

            if (item.itemId == R.id.navigation_home) {

                homeViewModel.savedCategory = "All"
                homeViewModel.savedPage = 1

                navController.popBackStack(R.id.navigation_home, false)
                true
            } else {
                navController.navigate(item.itemId)
                true
            }
        }

        // Drawer menu
        navigationView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.menu_home -> {
                    homeViewModel.savedCategory = "All"
                    navController.navigate(R.id.navigation_home)
                }

                R.id.menu_search ->
                    navController.navigate(R.id.searchFragment)

                R.id.menu_orders -> {
                    navController.navigate(R.id.navigation_account)
                    navController.navigate(R.id.myOrdersFragment)
                }


                R.id.cat_all -> openCategory("All")
                R.id.cat_phone -> openCategory("Phone")
                R.id.cat_laptop -> openCategory("Laptop")
                R.id.cat_clock -> openCategory("Clock")
                R.id.cat_pc -> openCategory("PC")
                R.id.cat_electronic -> openCategory("Electronic")

                R.id.menu_logout -> {
                    SessionManager(this).clearSession()
                    navController.navigate(R.id.loginFragment)
                }
            }

            drawerLayout.closeDrawer(GravityCompat.END)
            true
        }
    }

    private fun openCategory(type: String) {

        homeViewModel.savedCategory = type
        homeViewModel.savedPage = 1

        findNavController(R.id.nav_host_fragment_activity_main)
            .navigate(R.id.categoryFragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_drawer) {
            drawerLayout.openDrawer(GravityCompat.END)
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(drawerLayout) || super.onSupportNavigateUp()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
