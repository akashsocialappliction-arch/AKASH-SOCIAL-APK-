package com.example.data

import com.example.model.AudioTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MusicAudioRepository {

    private val allTracks = listOf(
        AudioTrack(
            id = "audio_1",
            title = "Just Look Up",
            artist = "LNDÖ",
            durationSeconds = 30,
            category = "Trending",
            licenseInfo = "Creator Social Media License • 100% Cleared for Posts & Short Videos",
            waveformPreview = listOf(0.3f, 0.6f, 0.9f, 0.85f, 0.7f, 0.95f, 0.6f, 0.8f, 0.75f, 0.5f, 0.8f, 0.4f)
        ),
        AudioTrack(
            id = "audio_2",
            title = "Imagination",
            artist = "PALA.",
            durationSeconds = 32,
            category = "Lo-Fi Chill",
            licenseInfo = "Royalty-Free Creator License • Cleared for In-App Sharing",
            waveformPreview = listOf(0.4f, 0.5f, 0.65f, 0.7f, 0.6f, 0.8f, 0.7f, 0.65f, 0.55f, 0.45f, 0.6f, 0.35f)
        ),
        AudioTrack(
            id = "audio_3",
            title = "Scenery",
            artist = "Reveille",
            durationSeconds = 40,
            category = "Acoustic",
            licenseInfo = "Commercial Safe Creator Audio • Attribution Cleared",
            waveformPreview = listOf(0.25f, 0.45f, 0.6f, 0.75f, 0.85f, 0.9f, 0.8f, 0.7f, 0.55f, 0.4f, 0.6f, 0.3f)
        ),
        AudioTrack(
            id = "audio_4",
            title = "Starlight Pulse",
            artist = "Aarav Studio",
            durationSeconds = 32,
            category = "Synthwave",
            licenseInfo = "AKASH SOCIAL Creator Royalty-Free License • Cleared for All Feed Posts",
            waveformPreview = listOf(0.2f, 0.5f, 0.8f, 0.95f, 0.7f, 0.85f, 0.6f, 0.9f, 0.75f, 0.4f, 0.7f, 0.3f)
        ),
        AudioTrack(
            id = "audio_5",
            title = "Kinetic Motion Beat",
            artist = "Akash Studio",
            durationSeconds = 28,
            category = "Trending",
            licenseInfo = "Original Creator Beat • 100% Free Sharing Cleared",
            waveformPreview = listOf(0.5f, 0.7f, 0.9f, 0.85f, 0.6f, 0.95f, 0.7f, 0.8f, 0.6f, 0.75f, 0.5f, 0.4f)
        ),
        AudioTrack(
            id = "audio_6",
            title = "Midnight Cyber Highway",
            artist = "Nagod Waves",
            durationSeconds = 45,
            category = "Electronic",
            licenseInfo = "Licensed Community Audio • Copyright Cleared for Short-Form Content",
            waveformPreview = listOf(0.4f, 0.6f, 0.7f, 0.9f, 0.95f, 0.8f, 0.65f, 0.75f, 0.5f, 0.6f, 0.8f, 0.4f)
        ),
        AudioTrack(
            id = "audio_7",
            title = "Golden Hour Breeze",
            artist = "Kavya Mishra Sound",
            durationSeconds = 38,
            category = "Acoustic",
            licenseInfo = "Creative Commons Zero (CC0) • Open Platform Music",
            waveformPreview = listOf(0.25f, 0.45f, 0.65f, 0.8f, 0.75f, 0.7f, 0.85f, 0.6f, 0.45f, 0.3f, 0.5f, 0.2f)
        ),
        AudioTrack(
            id = "audio_8",
            title = "Deep Space Ambient Vibe",
            artist = "Akash Beats & Ambient",
            durationSeconds = 50,
            category = "Ambient",
            licenseInfo = "AKASH SOCIAL Original Creator Sound • Free Attribution",
            waveformPreview = listOf(0.2f, 0.3f, 0.45f, 0.6f, 0.7f, 0.8f, 0.75f, 0.6f, 0.4f, 0.3f, 0.25f, 0.2f)
        )
    )

    private val _tracks = MutableStateFlow(allTracks)
    val tracks: StateFlow<List<AudioTrack>> = _tracks.asStateFlow()

    private val _playingTrackId = MutableStateFlow<String?>(null)
    val playingTrackId: StateFlow<String?> = _playingTrackId.asStateFlow()

    fun togglePlay(trackId: String) {
        if (_playingTrackId.value == trackId) {
            _playingTrackId.value = null
        } else {
            _playingTrackId.value = trackId
        }
    }

    fun stopAudio() {
        _playingTrackId.value = null
    }

    fun filterTracks(query: String, category: String? = null) {
        var filtered = allTracks
        if (!category.isNullOrBlank() && category != "All") {
            filtered = filtered.filter { it.category.equals(category, ignoreCase = true) }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            filtered = filtered.filter {
                it.title.lowercase().contains(q) ||
                it.artist.lowercase().contains(q) ||
                it.category.lowercase().contains(q)
            }
        }
        _tracks.value = filtered
    }

    fun addOriginalTrack(title: String, artist: String, category: String = "Original", durationSeconds: Int = 30): AudioTrack {
        val newTrack = AudioTrack(
            id = "user_audio_${System.currentTimeMillis()}",
            title = title.trim(),
            artist = artist.trim(),
            durationSeconds = durationSeconds,
            category = category,
            licenseInfo = "Creator Original Sound • Authorized for AKASH SOCIAL Posts & Reels",
            waveformPreview = listOf(0.3f, 0.5f, 0.8f, 0.7f, 0.9f, 0.6f, 0.85f, 0.4f, 0.75f, 0.5f, 0.7f, 0.3f)
        )
        _tracks.value = listOf(newTrack) + _tracks.value
        return newTrack
    }
}
