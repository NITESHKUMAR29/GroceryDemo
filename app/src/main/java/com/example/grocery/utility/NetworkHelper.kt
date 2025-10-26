package com.example.grocery.utility

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import com.example.domain.utility.NetworkChecker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class NetworkHelper @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkChecker {

    override fun isNetworkConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnected == true
    }
}