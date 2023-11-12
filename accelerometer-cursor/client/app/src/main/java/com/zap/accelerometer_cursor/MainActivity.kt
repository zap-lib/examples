package com.zap.accelerometer_cursor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.github.zap_lib.ZapClient
import com.github.zap_lib.resources.ZapAccelerometer
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.net.InetAddress

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var control: PlaygroundView
    private lateinit var sensorManager: SensorManager
    private var zap: ZapClient? = null

    private val qrLauncher = registerForActivityResult(ScanContract()) {
        zap = ZapClient(InetAddress.getByName(it.contents.toString()))
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        control = findViewById(R.id.playground)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        findViewById<Button>(R.id.connectButton).setOnClickListener {
            qrLauncher.launch(ScanOptions())
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            control.x = event.values[0] * -30
            control.y = event.values[1] * 30
            zap?.send(ZapAccelerometer(event.values[0], event.values[1], event.values[2]))
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
        zap?.stop()
    }
}