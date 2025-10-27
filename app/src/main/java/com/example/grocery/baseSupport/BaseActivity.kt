package com.example.grocery.baseSupport

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

open class BaseActivity : AppCompatActivity() {

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
                top = systemBars.top,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            WindowInsetsCompat.CONSUMED
        }

        // Request to apply insets
        ViewCompat.requestApplyInsets(content)
    }
}