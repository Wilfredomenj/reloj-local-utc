
package com.relojutc

import android.app.*
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.*
import android.view.*
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class ClockService : Service() {

    private lateinit var wm: WindowManager
    private lateinit var clock: TextView

    private val handler = Handler(Looper.getMainLooper())

    private val update = object : Runnable {
        override fun run() {
            val date = Date()

            val local = SimpleDateFormat(
                "HH:mm:ss", Locale.getDefault()
            ).apply {
                timeZone = TimeZone.getTimeZone(
                    "America/Tegucigalpa"
                )
            }.format(date)

            val utc = SimpleDateFormat(
                "HH:mm:ss", Locale.getDefault()
            ).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(date)

            clock.text = "HONDURAS  $local     UTC  $utc"

            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            "reloj_utc",
            "Reloj flotante",
            NotificationManager.IMPORTANCE_LOW
        )

        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)

        val notification = Notification.Builder(
            this, "reloj_utc"
        )
            .setContentTitle("Reloj Local + UTC")
            .setContentText("Reloj flotante activo")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .build()

        startForeground(1, notification)

        wm = getSystemService(WINDOW_SERVICE) as WindowManager

        clock = TextView(this).apply {
            textSize = 22f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(20, 30, 45))
            setPadding(24, 18, 24, 18)
            gravity = Gravity.CENTER
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP

        wm.addView(clock, params)

        handler.post(update)
    }

    override fun onDestroy() {
        handler.removeCallbacks(update)

        if (::clock.isInitialized && clock.parent != null) {
            wm.removeView(clock)
        }

        super.onDestroy()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int = START_STICKY

    override fun onBind(intent: Intent?) = null
}
