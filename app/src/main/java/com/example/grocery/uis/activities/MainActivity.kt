package com.example.grocery.uis.activities

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.grocery.R
import com.example.grocery.databinding.ActivityMainBinding
import com.example.grocery.uis.fragments.AllFragment
import com.example.grocery.uis.fragments.GroceryFragment
import com.example.grocery.uis.fragments.StationaryFragment
import com.example.grocery.uis.fragments.SweetsFragment
import com.example.grocery.viewModels.ProductListViewModel
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.grocery.baseSupport.BaseActivity
import com.example.grocery.states.UiState
import com.example.grocery.utility.CategoryIds
import kotlinx.coroutines.flow.collect

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

        binding.mainToolbar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var scrollRange = -1
            val collapsedColor = ContextCompat.getColor(this@MainActivity, R.color.light_gray)
            val expandedColor = "#003F94".toColorInt()

            override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout?.totalScrollRange ?: 0
                }

                val fraction = -verticalOffset / scrollRange.toFloat()


                val bgColor = ArgbEvaluator().evaluate(fraction, expandedColor, collapsedColor) as Int
                binding.mainToolbar.setBackgroundColor(bgColor)
                binding.tabBar.setBackgroundColor(bgColor)


                val indicatorColor = ArgbEvaluator().evaluate(
                    fraction,
                    ContextCompat.getColor(this@MainActivity, R.color.white),
                    ContextCompat.getColor(this@MainActivity, R.color.black)
                ) as Int
                binding.tabBar.setSelectedTabIndicatorColor(indicatorColor)


                val selectedColor = ArgbEvaluator().evaluate(
                    fraction,
                    ContextCompat.getColor(this@MainActivity, R.color.white),
                    ContextCompat.getColor(this@MainActivity, R.color.black)
                ) as Int
                val unselectedColor = ArgbEvaluator().evaluate(
                    fraction,
                    ContextCompat.getColor(this@MainActivity, R.color.text_secondary_light),
                    ContextCompat.getColor(this@MainActivity, R.color.text_secondary_light)
                ) as Int

                for (i in 0 until binding.tabBar.tabCount) {
                    val tab = binding.tabBar.getTabAt(i)
                    val textView = tab?.customView?.findViewById<TextView>(R.id.page_text)
                    textView?.setTextColor(if (tab.isSelected) selectedColor else unselectedColor)
                }


                val textColor = ArgbEvaluator().evaluate(fraction, Color.WHITE, Color.BLACK) as Int
                binding.appTitle.setTextColor(textColor)
                binding.locationText.setTextColor(textColor)
            }
        })

        viewModel.getCategories()
        observeCart()
        observeCategories()

        binding.searchProduct.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }


    }

    private fun setupTabs(tabLayout: TabLayout) {
        val tabs = listOf("All", "Grocery", "Stationary", "Sweets")

        val tabIcons = mapOf(
            "All" to R.drawable.all_service,
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


                binding.mainToolbar.setExpanded(true, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.findViewById<TextView>(R.id.page_text)
                    ?.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white_50))
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                binding.mainToolbar.setExpanded(true, true)
            }
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

    private fun observeCategories(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.productCategoryState.collect { state ->
                    if (state is UiState.Success) {
                        val categories = state.data
                        categories.forEach { category ->
                            when (category.name) {
                                "Grocery" -> CategoryIds.GROCERY = category.id
                                "Stationary" -> CategoryIds.STATIONARY = category.id
                                "Sweets" -> CategoryIds.SWEETS = category.id
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onContentChanged() {
        super.onContentChanged()
        applyEdgeToEdge(findViewById(android.R.id.content))
    }
    private fun applyEdgeToEdge(content: View?) {
        if (content == null) return

        ViewCompat.setOnApplyWindowInsetsListener(content) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updatePadding(
                left = systemBars.left,
                top = 0,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(content)
    }

}