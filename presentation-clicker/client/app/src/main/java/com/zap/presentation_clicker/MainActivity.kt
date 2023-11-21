package com.zap.presentation_clicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import com.github.zap_lib.ZapClient
import com.github.zap_lib.resources.ZapUiEvent
import com.google.android.material.button.MaterialButton
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.net.InetAddress

class MainActivity : AppCompatActivity() {
    private var zap: ZapClient? = null

    private val qrLauncher = registerForActivityResult(ScanContract()) {
        zap = ZapClient(InetAddress.getByName(it.contents.toString()))
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<MaterialButton>(R.id.connectButton).setOnClickListener {
            qrLauncher.launch(ScanOptions())
        }

        findViewById<MaterialButton>(R.id.nextButton).setOnClickListener {
            zap?.send(ZapUiEvent("next", ZapUiEvent.Event.CLICK))
        }

        findViewById<MaterialButton>(R.id.prevButton).setOnClickListener {
            zap?.send(ZapUiEvent("prev", ZapUiEvent.Event.CLICK))
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            zap?.send(ZapUiEvent("next", ZapUiEvent.Event.CLICK))
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            zap?.send(ZapUiEvent("prev", ZapUiEvent.Event.CLICK))
        }

        return true
    }

    override fun onStop() {
        super.onStop()
        zap?.stop()
    }
}
