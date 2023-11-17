package com.zap_lib.zap_example

import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.zap_lib.MetaInfo
import com.github.zap_lib.ZapServer
import com.github.zap_lib.resources.ZapAccelerometer
import com.zap_lib.zap_example.widgets.PlaygroundView

class DisplayActivity : AppCompatActivity() {
    private lateinit var target: PlaygroundView
    private lateinit var zap: ZapServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        target = findViewById(R.id.playground)
        target.add(POINTER_ID, Paint().apply { color = Color.RED })

        zap = object : ZapServer() {
            override fun onAccelerometerReceived(info: MetaInfo, data: ZapAccelerometer) {
                target.moveTo(POINTER_ID, x = target.get(POINTER_ID).x - data.x * 2f)
                target.moveTo(POINTER_ID, y = target.get(POINTER_ID).y + data.y * 2f)
            }
        }.also { it.listen() }
    }

    override fun onStop() {
        super.onStop()
        zap.stop()
    }

    companion object {
        private const val POINTER_ID = "0"
    }
}
