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
import android.widget.Switch
import com.zap_lib.core.ZapClient
import com.zap_lib.core.resources.ZapAccelerometer
import com.zap_lib.core.resources.ZapUiComponent
import java.net.InetAddress

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var zap: ZapClient
    private lateinit var sensorManager: SensorManager

    private var useAcc = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        zap = ZapClient(InetAddress.getByName("192.168.35.213"))

        registerButtonTouchListeners()
        findViewById<Switch>(R.id.accSwitch).setOnCheckedChangeListener { _, b -> useAcc = b }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (useAcc && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val (x, y, z) = event.values
            zap.send(ZapAccelerometer(x, y, z))
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
        zap.stop()
    }

    private fun registerButtonTouchListeners() {
        val buttonTouchListener = { view: View, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    ZapUiComponent.Event.CLICK_DOWN
                }
                MotionEvent.ACTION_UP -> {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE)
                    ZapUiComponent.Event.CLICK_UP
                }
                else -> null
            }?.let { zapEvent ->
                zap.send(ZapUiComponent(view.resources.getResourceName(view.id), zapEvent))
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
