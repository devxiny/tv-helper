package com.tools.tvhelper

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.tools.tvhelper.server.TvHttpServer
import com.tools.tvhelper.ui.TvHelperDialog
import com.tools.tvhelper.utils.NetworkUtils

object TvHelper {

    private const val DEFAULT_PORT = 8989
    private const val MAX_RETRY_COUNT = 20

    @SuppressLint("StaticFieldLeak")
    private var server: TvHttpServer? = null
    private var dialog: TvHelperDialog? = null
    private var port: Int = DEFAULT_PORT

    fun startServer(
        context: Context,
        port: Int = DEFAULT_PORT,
        config: TvControlConfig,
        listener: (String, Map<String, String>?) -> Unit
    ) {
        if (server != null) return

        var currentPort = port
        var retryCount = 0

        val mainHandler = Handler(Looper.getMainLooper())
        val safeListener: (String, Map<String, String>?) -> Unit = { action, data ->
            mainHandler.post {
                listener(action, data)
            }
        }

        while (retryCount < MAX_RETRY_COUNT) {
            try {
                // Use applicationContext to avoid memory leaks since TvHelper is a singleton
                val s = TvHttpServer(context.applicationContext, currentPort, config, safeListener)
                s.start()
                server = s
                this.port = currentPort
                Log.d("TvHelper", "Server started on port $currentPort")
                return
            } catch (e: java.io.IOException) {
                Log.w("TvHelper", "Port $currentPort is occupied, trying next port...")
                currentPort++
                retryCount++
            }
        }
        Log.e("TvHelper", "Failed to start server after $MAX_RETRY_COUNT attempts")
    }

    fun stopServer() {
        server?.stop()
        server = null
    }

    fun isRunning(): Boolean = server != null

    fun showDialog(activity: FragmentActivity) {
        if (!isRunning()) {
            // Ideally we should warn or auto-start, but for now we just return or log
            Log.w("TvHelper", "Server is not running. Call startServer() first.")
            return
        }

        if (dialog == null) {
            dialog = TvHelperDialog.newInstance()
        }

        if (dialog?.isAdded == true) return

        // Check if already shown to avoid crashes or duplicates
        val fm = activity.supportFragmentManager
        if (fm.findFragmentByTag("TvHelperDialog") == null) {
            dialog?.show(fm, "TvHelperDialog")
        }
    }

    fun hideDialog() {
        dialog?.dismiss()
        dialog = null
    }

    fun onDialogDismissed() {
        dialog = null
    }

    private var toggleKey: Int = -1

    fun setToggleKey(keyCode: Int) {
        this.toggleKey = keyCode
    }

    fun getToggleKey(): Int {
        return toggleKey
    }

    fun handleKeyEvent(activity: FragmentActivity, event: KeyEvent?): Boolean {
        if (event == null) return false
        if (toggleKey != -1 && event.keyCode == toggleKey && event.action == KeyEvent.ACTION_UP) {
            toggleDialog(activity)
            return true
        }
        return false
    }

    private fun toggleDialog(activity: FragmentActivity) {
        if (dialog != null && dialog!!.isAdded) {
            hideDialog()
        } else {
            showDialog(activity)
        }
    }

    fun getServerUrl(context: Context): String? {
        if (!isRunning()) return null
        val ip = NetworkUtils.getIpAddress(context) ?: return null
        return "http://$ip:$port"
    }
}
