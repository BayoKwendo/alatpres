package com.alat.helpers

import android.content.Context
import android.net.ConnectivityManager


class NetworkCheck {
     var mContext: Context? = null

     fun isNetworkAvailable(): Boolean {
          // Using ConnectivityManager to check for Network Connection
        val connectivityManager = (mContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
        val activeNetworkInfo = connectivityManager
                .activeNetworkInfo
        return activeNetworkInfo != null
    }

}
