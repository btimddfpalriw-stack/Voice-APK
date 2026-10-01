package com.example.persianvoiceassistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var status: TextView

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val micGranted = result[Manifest.permission.RECORD_AUDIO] == true ||
            checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (micGranted) startAssistant() else status.text = "اجازه میکروفون لازم است"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        status = findViewById(R.id.status)

        findViewById<Button>(R.id.start).setOnClickListener { requestPermissionsAndStart() }
        findViewById<Button>(R.id.stop).setOnClickListener {
            stopService(Intent(this, AssistantService::class.java))
            status.text = "دستیار متوقف شد"
        }
    }

    private fun requestPermissionsAndStart() {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= 33) permissions += Manifest.permission.POST_NOTIFICATIONS

        val missing = permissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isEmpty()) startAssistant()
        else permissionLauncher.launch(missing.toTypedArray())
    }

    private fun startAssistant() {
        val intent = Intent(this, AssistantService::class.java)
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(intent) else startService(intent)
        status.text = "دستیار در پس‌زمینه فعال شد"
    }
}
