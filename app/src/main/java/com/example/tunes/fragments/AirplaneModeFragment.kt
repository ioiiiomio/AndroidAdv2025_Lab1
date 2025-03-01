package com.example.tunes.fragments
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tunes.R


class AirplaneModeFragment : Fragment() {

    private var statusTextView: TextView? = null
    private var statusImageView: ImageView? = null
    private var airplaneModeReceiver: BroadcastReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_airplane_mode, container, false)
        statusTextView = view.findViewById(R.id.status_text)
        statusImageView = view.findViewById(R.id.status_image)
        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI() // Update UI when returning to app
        registerAirplaneModeReceiver()
    }

    private fun updateUI() {
        val isAirplaneModeOn = Settings.System.getInt(
            requireContext().contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0

        if (isAirplaneModeOn) {
            statusTextView?.text = "You are Offline"
            statusImageView?.setImageResource(R.drawable.ic_offline)
        } else {
            statusTextView?.text = "You are Online!"
            statusImageView?.setImageResource(R.drawable.ic_online)
        }
    }


    override fun onPause() {
        super.onPause()
        airplaneModeReceiver?.let { requireContext().unregisterReceiver(it) }
    }

    private fun registerAirplaneModeReceiver() {
        val filter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        airplaneModeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
                    val isAirplaneModeOn = Settings.System.getInt(
                        context.contentResolver,
                        Settings.Global.AIRPLANE_MODE_ON, 0
                    ) != 0

                    if (isAirplaneModeOn) {
                        statusTextView?.text = "You are Offline"
                        statusImageView?.setImageResource(R.drawable.ic_offline)
                    } else {
                        statusTextView?.text = "You are Online!"
                        statusImageView?.setImageResource(R.drawable.ic_online)
                    }
                    Toast.makeText(context, statusTextView?.text, Toast.LENGTH_SHORT).show()
                }
            }
        }
        requireContext().registerReceiver(airplaneModeReceiver, filter)
    }
}
