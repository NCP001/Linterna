package com.example.linterna

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.linterna.R

class MainActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private var isFlashOn = false
    private var blinkHandler: Handler? = null
    private var isBlinking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 50)
        }

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]

        val btnTurnOn: Button = findViewById(R.id.btnTurnOn)
        val btnTurnOff: Button = findViewById(R.id.btnTurnOff)
        val btnBlinkSlow: Button = findViewById(R.id.btnBlinkSlow)
        val btnBlinkFast: Button = findViewById(R.id.btnBlinkFast)

        btnTurnOn.setOnClickListener {
            turnOnFlashlight()
        }

        btnTurnOff.setOnClickListener {
            turnOffFlashlight()
        }

        btnBlinkSlow.setOnClickListener {
            blinkFlashlight(1000)
        }

        btnBlinkFast.setOnClickListener {
            blinkFlashlight(300)
        }
    }

    private fun turnOnFlashlight() {
        try {
            if (!isFlashOn) {
                cameraManager.setTorchMode(cameraId, true)
                isFlashOn = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun turnOffFlashlight() {
        try {
            if (isFlashOn) {
                cameraManager.setTorchMode(cameraId, false)
                isFlashOn = false
            }
            stopBlinking()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun blinkFlashlight(interval: Long) {
        if (isBlinking) return

        blinkHandler = Handler(Looper.getMainLooper())
        val blinkRunnable = object : Runnable {
            override fun run() {
                if (isFlashOn) {
                    turnOffFlashlight()
                } else {
                    turnOnFlashlight()
                }
                blinkHandler?.postDelayed(this, interval)
            }
        }
        blinkHandler?.post(blinkRunnable)
        isBlinking = true
    }

    private fun stopBlinking() {
        blinkHandler?.removeCallbacksAndMessages(null)
        isBlinking = false
    }
}