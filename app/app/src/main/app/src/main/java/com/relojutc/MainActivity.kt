
package com.relojutc

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.view.Gravity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(30, 30, 30, 30)
        }

        val title = TextView(this).apply {
            text = "RELOJ LOCAL + UTC"
            textSize = 25f
            gravity = Gravity.CENTER
        }

        val start = Button(this).apply {
            text = "ACTIVAR RELOJ FLOTANTE"
        }

        val stop = Button(this).apply {
            text = "DETENER RELOJ"
        }

        layout.addView(title)
        layout.addView(start)
        layout.addView(stop)

        setContentView(layout)

        start.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                val service = Intent(this, ClockService::class.java)
                startForegroundService(service)
            }
        }

        stop.setOnClickListener {
            stopService(Intent(this, ClockService::class.java))
        }
    }
}
