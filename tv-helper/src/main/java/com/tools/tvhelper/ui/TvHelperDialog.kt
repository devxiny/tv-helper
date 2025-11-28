package com.tools.tvhelper.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.tools.tvhelper.R
import com.tools.tvhelper.TvHelper
import com.tools.tvhelper.utils.QrCodeGenerator

class TvHelperDialog : DialogFragment() {

    companion object {
        fun newInstance(): TvHelperDialog {
            return TvHelperDialog()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_tv_helper, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = TvHelper.getServerUrl(requireContext())
        if (url == null) {
            view.findViewById<TextView>(R.id.tv_url).text = "Server not running or No Wi-Fi"
            return
        }

        view.findViewById<TextView>(R.id.tv_url).text = url

        val qrBitmap = QrCodeGenerator.generate(url)
        view.findViewById<ImageView>(R.id.iv_qr_code).setImageBitmap(qrBitmap)
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        TvHelper.onDialogDismissed()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): android.app.Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP) {
                // Check if this is the toggle key
                val toggleKey = TvHelper.getToggleKey()
                if (toggleKey != -1 && keyCode == toggleKey) {
                    dismiss()
                    return@setOnKeyListener true
                }
            }
            false
        }
        return dialog
    }
}
