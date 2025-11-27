package com.tools.tvhelper

import androidx.fragment.app.FragmentActivity
import com.tools.tvhelper.ui.TvHelperDialog

object TvHelper {
    fun show(activity: FragmentActivity, config: TvControlConfig, listener: (String, Map<String, String>?) -> Unit) {
        val dialog = TvHelperDialog.newInstance(config)
        dialog.setListener(listener)
        dialog.show(activity.supportFragmentManager, "TvHelperDialog")
    }
}
