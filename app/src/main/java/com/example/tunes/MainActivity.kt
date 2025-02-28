package com.example.tunes
import android.Manifest

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.tunes.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }


        findViewById<Button>(R.id.btnMusic).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.musicPlayerFragment)
        }

        findViewById<Button>(R.id.btnShare).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.shareFragment)
        }

        findViewById<Button>(R.id.btnBroadcast).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.broadcastFragment)
        }

        findViewById<Button>(R.id.btnEvents).setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.eventsFragment)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                android.util.Log.d("MainActivity", "Notification permission granted")
            } else {
                android.util.Log.w("MainActivity", "Notification permission denied")
            }
        }
    }

}
