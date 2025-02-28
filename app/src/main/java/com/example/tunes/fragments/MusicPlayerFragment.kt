package com.example.tunes.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.tunes.R

// Data class to store track information
data class Track(val name: String, val artist: String, val fileResId: Int, val albumArt: Int)

class MusicPlayerFragment : androidx.fragment.app.Fragment() {
    private lateinit var mediaPlayer: MediaPlayer
    private var currentIndex = 0

    private val tracks = listOf(
        Track("Song One", "Artist A", R.raw.song1, R.drawable.cover_1),
        Track("Song Two", "Artist B", R.raw.song2, R.drawable.cover_2),
        Track("Song Three", "Artist C", R.raw.song3, R.drawable.cover_3)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_music_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playButton: Button = view.findViewById(R.id.playButton)
        val nextButton: Button = view.findViewById(R.id.nextButton)
        val prevButton: Button = view.findViewById(R.id.prevButton)

        mediaPlayer = MediaPlayer()
        loadTrack(currentIndex)

        playButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
        }


        nextButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % tracks.size
            loadTrack(currentIndex)
            mediaPlayer.start()
        }

        prevButton.setOnClickListener {
            currentIndex = if (currentIndex - 1 < 0) tracks.size - 1 else currentIndex - 1
            loadTrack(currentIndex)
            mediaPlayer.start()
        }
    }

    private fun loadTrack(index: Int) {
        val track = tracks[index]

        // Release and reset media player
        mediaPlayer.reset()
        mediaPlayer.release()
        mediaPlayer = MediaPlayer.create(requireContext(), track.fileResId)

        // Update UI elements
        val albumArtView: ImageView = requireView().findViewById(R.id.albumArt)
        val trackNameView: TextView = requireView().findViewById(R.id.trackName)
        val artistNameView: TextView = requireView().findViewById(R.id.artistName)

        albumArtView.setImageResource(track.albumArt)
        trackNameView.text = track.name
        artistNameView.text = track.artist
    }



    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer.release()
    }
}
