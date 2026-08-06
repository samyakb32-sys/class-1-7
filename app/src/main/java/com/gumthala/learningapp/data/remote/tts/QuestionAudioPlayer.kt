package com.gumthala.learningapp.data.remote.tts

import android.content.Context
import android.media.MediaPlayer
import com.gumthala.learningapp.domain.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class PlaybackState { IDLE, LOADING, PLAYING, ERROR }

/**
 * Backs the Listen / 🔊 button on each quiz question: fetches (or reuses cached)
 * Pollinations TTS audio and plays it with MediaPlayer.
 */
class QuestionAudioPlayer(context: Context) {
    private val ttsClient = PollinationsTtsClient(context)
    private var mediaPlayer: MediaPlayer? = null

    private val _state = MutableStateFlow(PlaybackState.IDLE)
    val state: StateFlow<PlaybackState> = _state

    suspend fun play(text: String, language: AppLanguage) {
        stop()
        _state.value = PlaybackState.LOADING
        val result = ttsClient.fetchAudio(text, language)
        val file = result.getOrNull()
        if (file == null) {
            _state.value = PlaybackState.ERROR
            return
        }
        runCatching {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnCompletionListener { _state.value = PlaybackState.IDLE }
                setOnErrorListener { _, _, _ -> _state.value = PlaybackState.ERROR; true }
                prepare()
                start()
            }
            _state.value = PlaybackState.PLAYING
        }.onFailure {
            _state.value = PlaybackState.ERROR
        }
    }

    fun stop() {
        mediaPlayer?.apply {
            runCatching { if (isPlaying) stop() }
            release()
        }
        mediaPlayer = null
        _state.value = PlaybackState.IDLE
    }
}
