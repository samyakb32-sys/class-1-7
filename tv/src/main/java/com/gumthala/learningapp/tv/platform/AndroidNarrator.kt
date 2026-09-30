package com.gumthala.learningapp.tv.platform

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import com.gumthala.learningapp.tv.core.Narrator
import com.gumthala.learningapp.tv.core.speakingTimeMs
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume

/**
 * Sunny's voice, using whatever speech engine the TV has. Plenty of cheap TV boxes have none, or only
 * an English voice, so every failure path degrades to "wait as long as speaking would take": the lesson
 * still plays at the right pace with captions, it just isn't read aloud.
 */
class AndroidNarrator(context: Context) : Narrator {
    private var tts: TextToSpeech? = null

    @Volatile private var ready = false
    @Volatile private var languageOk = false
    private val counter = AtomicInteger()
    private val pending = ConcurrentHashMap<String, CancellableContinuation<Unit>>()

    override val available: Boolean get() = ready && languageOk

    override var language: String = "en"
        set(value) {
            field = value
            applyLanguage()
        }

    override var slow: Boolean = true
        set(value) {
            field = value
            tts?.setSpeechRate(rate())
        }

    // 0.78 sounded robotic; a gentle 0.88 is still easy to follow for little ears but keeps natural rhythm.
    private fun rate() = if (slow) 0.88f else 1.0f

    private val listener = object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {}
        override fun onDone(utteranceId: String?) { finish(utteranceId) }

        @Deprecated("Required by the abstract class on older API levels")
        override fun onError(utteranceId: String?) { finish(utteranceId) }
        override fun onError(utteranceId: String?, errorCode: Int) { finish(utteranceId) }
        override fun onStop(utteranceId: String?, interrupted: Boolean) { finish(utteranceId) }
    }

    private fun finish(id: String?) {
        if (id == null) return
        val cont = pending.remove(id) ?: return
        if (cont.isActive) cont.resume(Unit)
    }

    init {
        try {
            tts = TextToSpeech(context.applicationContext) { status ->
                val engine = tts
                if (status == TextToSpeech.SUCCESS && engine != null) {
                    engine.setOnUtteranceProgressListener(listener)
                    engine.setPitch(1.0f)
                    engine.setSpeechRate(rate())
                    ready = true
                    applyLanguage()
                }
            }
        } catch (e: Exception) {
            ready = false
        }
    }

    private fun applyLanguage() {
        val engine = tts ?: return
        if (!ready) return
        val locale = when (language) {
            "hi" -> Locale("hi", "IN")
            "mr" -> Locale("mr", "IN")
            else -> Locale("en", "IN")
        }
        var result = engine.setLanguage(locale)
        if (language == "en" && (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
            result = engine.setLanguage(Locale.US)
        }
        // For Hindi / Marathi, silence beats reading Devanagari with an English voice.
        languageOk = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        if (languageOk) bestOfflineVoice(engine, locale)?.let {
            try { engine.setVoice(it) } catch (_: Exception) { }
        }
    }

    /**
     * The most natural voice that works with no internet: same language (same country first, so en-IN
     * beats en-US for an Indian child), installed on the TV, and the highest quality on offer.
     * Network voices are skipped so the lesson never stalls waiting for a connection.
     */
    private fun bestOfflineVoice(engine: TextToSpeech, locale: Locale): Voice? = try {
        engine.voices.orEmpty()
            .filter { v ->
                v.locale.language == locale.language &&
                    !v.isNetworkConnectionRequired &&
                    v.features?.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED) != true
            }
            .sortedWith(compareByDescending<Voice> { it.locale.country == locale.country }.thenByDescending { it.quality }.thenBy { it.latency })
            .firstOrNull()
    } catch (e: Exception) {
        null
    }

    override suspend fun say(text: String) {
        val engine = tts
        if (engine == null || !available || text.isBlank()) {
            delay(speakingTimeMs(text))
            return
        }
        val id = "u" + counter.incrementAndGet()
        val limit = speakingTimeMs(text) * 3 + 4000
        val completed = withTimeoutOrNull(limit) {
            suspendCancellableCoroutine<Unit> { cont ->
                pending[id] = cont
                cont.invokeOnCancellation {
                    pending.remove(id)
                    try { engine.stop() } catch (_: Exception) { }
                }
                val r = try {
                    engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, id)
                } catch (e: Exception) {
                    TextToSpeech.ERROR
                }
                if (r == TextToSpeech.ERROR) finish(id)
            }
        }
        pending.remove(id)
        if (completed == null) {
            try { engine.stop() } catch (_: Exception) { }
        }
    }

    override fun stop() {
        try { tts?.stop() } catch (_: Exception) { }
        for (c in pending.values) if (c.isActive) c.resume(Unit)
        pending.clear()
    }

    fun shutdown() {
        stop()
        try { tts?.shutdown() } catch (_: Exception) { }
        tts = null
    }
}
