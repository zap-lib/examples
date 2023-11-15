package com.zap.motion_controller

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.github.zap_lib.ZapClient
import com.github.zap_lib.resources.ZapAccelerometer
import com.github.zap_lib.resources.ZapUiEvent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.net.InetAddress

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var zap: ZapClient? = null
    private lateinit var sensorManager: SensorManager

    private var useAcc = false

    private val qrLauncher = registerForActivityResult(ScanContract()) {
        zap = ZapClient(InetAddress.getByName(it.contents.toString()))
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        findViewById<FloatingActionButton>(R.id.connectButton).setOnClickListener {
            qrLauncher.launch(ScanOptions())
        }

        registerButtonTouchListeners()
        findViewById<SwitchMaterial>(R.id.accSwitch).setOnCheckedChangeListener { _, b -> useAcc = b }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (useAcc && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val (x, y, z) = event.values
            zap?.send(ZapAccelerometer(x, y, z))
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onStart() {
        super.onStart()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME,
        )
    }

    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)
        zap?.stop()
    }

    private fun registerButtonTouchListeners() {
        val buttonTouchListener = { view: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    ZapUiEvent.Event.CLICK_DOWN
                }
                MotionEvent.ACTION_UP -> {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE)
                    ZapUiEvent.Event.CLICK_UP
                }
                else -> null
            }?.let { zapEvent ->
                zap?.send(ZapUiEvent(view.resources.getResourceEntryName(view.id), zapEvent))
            }

            true
        }

        findViewById<Button>(R.id.buttonL).setOnTouchListener(buttonTouchListener)
        findViewById<Button>(R.id.buttonR).setOnTouchListener(buttonTouchListener)
        findViewById<Button>(R.id.buttonU).setOnTouchListener(buttonTouchListener)
        findViewById<Button>(R.id.buttonD).setOnTouchListener(buttonTouchListener)

        findViewById<Button>(R.id.buttonA).setOnTouchListener(buttonTouchListener)
        findViewById<Button>(R.id.buttonB).setOnTouchListener(buttonTouchListener)
        findViewById<Button>(R.id.buttonX).setOnTouchListener(buttonTouchListener)
        findViewById<Button>(R.id.buttonY).setOnTouchListener(buttonTouchListener)
    }
}
