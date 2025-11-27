package com.tools.tvhelper.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.tools.tvhelper.R
import com.tools.tvhelper.TvControlConfig
import com.tools.tvhelper.server.TvHttpServer
import com.tools.tvhelper.utils.NetworkUtils
import com.tools.tvhelper.utils.QrCodeGenerator
import java.util.Random

class TvHelperDialog : DialogFragment() {

    private var server: TvHttpServer? = null
    private var config: TvControlConfig? = null
    private var listener: ((String, Map<String, String>?) -> Unit)? = null

    companion object {
        fun newInstance(config: TvControlConfig): TvHelperDialog {
            val fragment = TvHelperDialog()
            val args = Bundle()
            args.putSerializable("config", config)
            fragment.arguments = args
            return fragment
        }
    }

    fun setListener(listener: (String, Map<String, String>?) -> Unit) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        config = arguments?.getSerializable("config") as? TvControlConfig
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_tv_helper, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val ip = NetworkUtils.getIpAddress(requireContext())
        if (ip == null) {
            view.findViewById<TextView>(R.id.tv_url).text = "No Wi-Fi Connection"
            return
        }

        val port = 8000 + Random().nextInt(1000)
        val url = "http://$ip:$port"
        
        view.findViewById<TextView>(R.id.tv_url).text = url
        
        val qrBitmap = QrCodeGenerator.generate(url)
        view.findViewById<ImageView>(R.id.iv_qr_code).setImageBitmap(qrBitmap)

        startServer(port)
    }

    private fun startServer(port: Int) {
        config?.let { cfg ->
            server = TvHttpServer(requireContext(), port, cfg) { action, data ->
                activity?.runOnUiThread {
                    listener?.invoke(action, data)
                }
            }
            try {
                server?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        server?.stop()
    }
}
