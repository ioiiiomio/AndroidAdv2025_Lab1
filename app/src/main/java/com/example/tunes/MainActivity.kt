package com.example.tunes

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.tunes.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}
