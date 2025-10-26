package com.example.grocery.uis.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.grocery.R
import com.example.grocery.baseSupport.BaseActivity
import com.example.grocery.databinding.ActivityMainBinding
import com.example.grocery.viewModels.ProductListViewModel
import com.example.grocery.states.UiState
import com.example.grocery.uis.fragments.AllFragment
import com.example.grocery.uis.fragments.GroceryFragment
import com.example.grocery.uis.fragments.StationaryFragment
import com.example.grocery.uis.fragments.SweetsFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ProductListViewModel by viewModels()



    companion object {
        const val ACTIVITY_TAG = "NewsListActivityss"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupTabs(binding.tabBar)
        binding.fabAddProduct.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }

        observeCart()

        binding.searchProduct.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }


    }

    private fun setupTabs(tabLayout: TabLayout) {
        val tabs = listOf("All", "Grocery", "Stationary", "Sweets")

        val tabIcons = mapOf(
            "All" to R.drawable.grocery,
            "Grocery" to R.drawable.grocery,
            "Stationary" to R.drawable.stationery,
            "Sweets" to R.drawable.sweets
        )

        val screenWidth = resources.displayMetrics.widthPixels
        val tabWidth = screenWidth / tabs.size

        for (tabTitle in tabs) {
            val tab = tabLayout.newTab()
            val view = LayoutInflater.from(this@MainActivity)
                .inflate(R.layout.item_home_tab, tabLayout, false)

            val textView = view.findViewById<TextView>(R.id.page_text)
            val imageView = view.findViewById<ImageView>(R.id.page_image)

            textView.text = tabTitle
            tabIcons[tabTitle]?.let { imageView.setImageResource(it) }


            view.layoutParams =
                LinearLayout.LayoutParams(tabWidth, LinearLayout.LayoutParams.WRAP_CONTENT)

            tab.customView = view
            tabLayout.addTab(tab)
        }

        tabLayout.tabMode = TabLayout.MODE_FIXED


        loadFragment("All")


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val title = tab.customView?.findViewById<TextView>(R.id.page_text)?.text.toString()
                loadFragment(title)
                tab.customView?.findViewById<TextView>(R.id.page_text)
                    ?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<TextView>(R.id.page_text)
                    ?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white_50))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


    @SuppressLint("CommitTransaction")
    private fun loadFragment(tabTitle: String) {
        val fragment = when (tabTitle) {
            "All" -> AllFragment()
            "Grocery" -> GroceryFragment()
            "Stationary" -> StationaryFragment()
            "Sweets" -> SweetsFragment()
            else -> AllFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun observeCart() {
        lifecycleScope.launch {
            viewModel.cartItems.collectLatest { cartItems ->
                val itemCount = cartItems.size
                binding.cartQuantityText.text = "$itemCount Items"

                updateMiniCartVisibility(itemCount > 0)

                binding.viewCartButton.setOnClickListener {
                    val intent = Intent(this@MainActivity, CartActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }




    private fun updateMiniCartVisibility(show: Boolean) {
        val layout = binding.bottomCartLayout
        if (show && layout.isGone) {
            layout.alpha = 0f
            layout.translationY = layout.height.toFloat()
            layout.visibility = View.VISIBLE
            layout.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .start()
        } else if (!show && layout.isVisible) {
            layout.animate()
                .alpha(0f)
                .translationY(layout.height.toFloat())
                .setDuration(300)
                .withEndAction { layout.visibility = View.GONE }
                .start()
        }
    }



}