package com.zap_lib.zap_example

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.zap_lib.ZapClient
import com.github.zap_lib.resources.ZapAccelerometer
import com.zap_lib.zap_example.widgets.PlaygroundView
import java.net.InetAddress

class ControllerActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var control: PlaygroundView
    private lateinit var sensorManager: SensorManager
    private lateinit var zap: ZapClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controller)

        control = findViewById(R.id.playground)
        control.add(POINTER_ID, Paint().apply { color = Color.BLUE })

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // TODO: Replace `0.0.0.0` with the address of your server device.
        zap = ZapClient(InetAddress.getByName("0.0.0.0"))
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            control.x = event.values[0] * -30
            control.y = event.values[1] * 30
            zap.send(ZapAccelerometer(event.values[0], event.values[1], event.values[2]))
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onStart() {
        super.onStart()
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)
        zap.stop()
    }

    companion object {
        private const val POINTER_ID = "0"
    }
}
