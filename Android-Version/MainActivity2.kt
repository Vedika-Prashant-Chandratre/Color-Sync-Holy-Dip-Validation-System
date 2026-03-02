package com.example.kumbhamela;

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kumbhamela.R
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.abs

class MainActivity2 : AppCompatActivity() {

    private lateinit var rootLayout: FrameLayout
    private lateinit var timeText: TextView

    // Example Slot
    private val slotId = "0440-0505"

    // Daily rotated secret (in production, securely delivered)
    private val dailyMasterKey = "KUMBH2027_MASTER_SECRET"

    // Simulated server time calibration
    private var timeOffset: Long = 0L

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootLayout = findViewById(R.id.rootLayout)
        timeText = findViewById(R.id.timeText)

        calibrateTime()
        startSignalLoop()
    }

    private fun calibrateTime() {
        val serverTime = System.currentTimeMillis()
        val localTime = System.currentTimeMillis()
        timeOffset = serverTime - localTime
    }

    private fun startSignalLoop() {
        handler.post(object : Runnable {
            override fun run() {
                updateVisualSignal()
                handler.postDelayed(this, updateInterval)
            }
        })
    }

    private fun updateVisualSignal() {
        val correctedTime = System.currentTimeMillis() + timeOffset
        val timeIndex = correctedTime / 1000

        val slotKey = hmacSHA256(dailyMasterKey, slotId)
        val signalHash = hmacSHA256(slotKey, "$slotId:$timeIndex")

        applyVisual(signalHash)
        timeText.text = "Time Index: $timeIndex"
    }

    private fun hmacSHA256(key: String, data: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(key.toByteArray(), "HmacSHA256")
        mac.init(secretKeySpec)
        val hashBytes = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun applyVisual(hash: String) {

        if (hash.length < 16) return

        try {

            val r = hash.substring(0, 2).toInt(16)
            val g = hash.substring(2, 4).toInt(16)
            val b = hash.substring(4, 6).toInt(16)

            val r2 = hash.substring(6, 8).toInt(16)
            val g2 = hash.substring(8, 10).toInt(16)
            val b2 = hash.substring(10, 12).toInt(16)

            val orientationIndex =
                hash.substring(12, 14).toInt(16) % GradientDrawable.Orientation.values().size

            val gradient = GradientDrawable(
                GradientDrawable.Orientation.values()[orientationIndex],
                intArrayOf(
                    Color.rgb(r, g, b),
                    Color.rgb(r2, g2, b2)
                )
            )

            gradient.cornerRadius = 0f
            rootLayout.background = gradient

            val scaleFactor =
                0.95f + (hash.substring(14, 16).toInt(16) / 255f) * 0.1f

            rootLayout.scaleX = scaleFactor
            rootLayout.scaleY = scaleFactor

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}