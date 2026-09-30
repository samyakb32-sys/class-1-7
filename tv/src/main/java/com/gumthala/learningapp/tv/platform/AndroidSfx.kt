package com.gumthala.learningapp.tv.platform

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.gumthala.learningapp.tv.core.Sfx
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

/**
 * Little pops, dings and fanfares, synthesised from sine waves at start-up, so the app ships no audio
 * files and needs no permissions. Any failure (no audio device, odd TV firmware) is swallowed: a missing
 * "ding" must never crash a lesson.
 */
class AndroidSfx : Sfx {
    private val rate = 22050
    private val pool = Executors.newCachedThreadPool()

    private fun tone(freq: Double, ms: Int, volume: Double = 0.45, slideTo: Double = freq): ShortArray {
        val n = rate * ms / 1000
        val out = ShortArray(n)
        var phase = 0.0
        for (i in 0 until n) {
            val f = freq + (slideTo - freq) * i / n
            phase += 2 * PI * f / rate
            val attack = min(1.0, i / (rate * 0.008))
            val release = min(1.0, (n - i) / (rate * 0.04))
            out[i] = (sin(phase) * attack * release * volume * Short.MAX_VALUE).toInt().toShort()
        }
        return out
    }

    private fun concat(vararg parts: ShortArray): ShortArray {
        val out = ShortArray(parts.sumOf { it.size })
        var at = 0
        for (p in parts) { p.copyInto(out, at); at += p.size }
        return out
    }

    private val tickSound by lazy { tone(1046.5, 45, 0.25) }
    private val correctSound by lazy { concat(tone(523.25, 90), tone(659.25, 90), tone(783.99, 160)) }
    private val wrongSound by lazy { tone(300.0, 260, 0.35, slideTo = 190.0) }
    private val starSound by lazy { concat(tone(1318.5, 70, 0.4), tone(1760.0, 130, 0.4)) }
    private val fanfareSound by lazy {
        concat(tone(523.25, 110), tone(659.25, 110), tone(783.99, 110), tone(1046.5, 320, 0.5))
    }

    private fun play(data: ShortArray) {
        pool.execute {
            try {
                @Suppress("DEPRECATION")
                val track = AudioTrack(
                    AudioManager.STREAM_MUSIC, rate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                    data.size * 2, AudioTrack.MODE_STATIC,
                )
                track.write(data, 0, data.size)
                track.play()
                Thread.sleep(data.size * 1000L / rate + 120)
                track.release()
            } catch (_: Throwable) {
            }
        }
    }

    override fun tick() = play(tickSound)
    override fun correct() = play(correctSound)
    override fun wrong() = play(wrongSound)
    override fun star() = play(starSound)
    override fun fanfare() = play(fanfareSound)
}
