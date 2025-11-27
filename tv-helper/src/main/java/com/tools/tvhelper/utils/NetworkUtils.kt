package com.tools.tvhelper.utils

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter

object NetworkUtils {
    fun getIpAddress(context: Context): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = wifiManager.connectionInfo.ipAddress
        return if (ipAddress == 0) null else Formatter.formatIpAddress(ipAddress)
    }
}
