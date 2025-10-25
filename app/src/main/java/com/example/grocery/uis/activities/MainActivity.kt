package com.example.grocery.uis.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.grocery.R
import com.example.grocery.baseSupport.BaseActivity
import com.example.grocery.databinding.ActivityMainBinding
import com.example.grocery.uis.fragments.AllFragment
import com.example.grocery.uis.fragments.GroceryFragment
import com.example.grocery.uis.fragments.StationaryFragment
import com.example.grocery.uis.fragments.SweetsFragment
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

private lateinit var binding: ActivityMainBinding
    companion object {
        const val ACTIVITY_TAG = "NewsListActivityss"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        setupTabs(binding.tabBar)

    }

    private fun setupTabs(tabLayout: TabLayout) {
        val tabs = listOf("All", "Grocery", "Stationary", "Sweets")

        // Add an image for each tab
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


            view.layoutParams = LinearLayout.LayoutParams(tabWidth, LinearLayout.LayoutParams.WRAP_CONTENT)

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

}