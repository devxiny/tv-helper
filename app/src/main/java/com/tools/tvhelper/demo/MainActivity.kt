package com.tools.tvhelper.demo

import android.os.Bundle
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tools.tvhelper.TvControlConfig
import com.tools.tvhelper.TvHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvLog = findViewById<TextView>(R.id.tv_log)

        val config = TvControlConfig.Builder()
            .setTitle("Demo Remote")
            .addButton("play", "Play/Pause", "primary")
            .addButton("next", "Next Video")
            .addInput("search_query", "Search...")
            .addButton("search", "Go", bindInput = "search_query")
            .build()
        TvHelper.setToggleKey(KeyEvent.KEYCODE_MENU)
        TvHelper.startServer(this, config = config) { action, data ->
            val log = "Action: $action, Data: $data"
            tvLog.append("\n$log")
            Toast.makeText(this, "Received: $action", Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.btn_start).setOnClickListener {
            TvHelper.showDialog(this)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (TvHelper.handleKeyEvent(this, event)) {
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        TvHelper.stopServer()
    }
}
