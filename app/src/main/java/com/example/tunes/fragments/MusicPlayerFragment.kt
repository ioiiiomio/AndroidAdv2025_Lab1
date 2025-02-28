package com.example.tunes.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.tunes.MusicService
import com.example.tunes.R

data class Track(val name: String, val artist: String, val fileResId: Int, val albumArt: Int)

class MusicPlayerFragment : Fragment() {
    private var currentIndex = 0

    private val tracks = listOf(
        Track("Think", "Kaleida", R.raw.song1, R.drawable.cover_1),
        Track("Nervous", "Crying City", R.raw.song2, R.drawable.cover_2),
        Track("Harvest Sky", "Oklou", R.raw.song3, R.drawable.cover_3)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playButton: Button = view.findViewById(R.id.playButton)
        val stopButton: Button = view.findViewById(R.id.stopButton)
        val nextButton: Button = view.findViewById(R.id.nextButton)
        val prevButton: Button = view.findViewById(R.id.prevButton)

        loadTrack(currentIndex)

        playButton.setOnClickListener {
            sendMusicCommand("PLAY", tracks[currentIndex].fileResId)
        }

        stopButton.setOnClickListener {
            sendMusicCommand("STOP")
        }

        nextButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % tracks.size
            loadTrack(currentIndex)
            sendMusicCommand("PLAY", tracks[currentIndex].fileResId)
        }

        prevButton.setOnClickListener {
            currentIndex = if (currentIndex - 1 < 0) tracks.size - 1 else currentIndex - 1
            loadTrack(currentIndex)
            sendMusicCommand("PLAY", tracks[currentIndex].fileResId)
        }
    }

    private fun sendMusicCommand(action: String, trackResId: Int? = null) {
        val intent = Intent(requireContext(), MusicService::class.java).apply {
            this.action = action
            trackResId?.let { putExtra("TRACK_ID", it) }
        }
        requireContext().startService(intent)
    }

    private fun loadTrack(index: Int) {
        val track = tracks[index]

        val albumArtView: ImageView = requireView().findViewById(R.id.albumArt)
        val trackNameView: TextView = requireView().findViewById(R.id.trackName)
        val artistNameView: TextView = requireView().findViewById(R.id.artistName)

        albumArtView.setImageResource(track.albumArt)
        trackNameView.text = track.name
        artistNameView.text = track.artist
    }
}
