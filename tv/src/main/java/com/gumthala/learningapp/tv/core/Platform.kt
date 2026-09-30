package com.gumthala.learningapp.tv.core

/**
 * Everything the UI needs from the device, as plain interfaces. The real Android versions live in
 * `platform/`; keeping the UI ignorant of Android is what lets it be screenshot-tested off-device.
 */
interface Narrator {
    /** True once a speech engine is ready (some cheap TV boxes ship without one). */
    val available: Boolean

    /** "en", "hi" or "mr". */
    var language: String
    var slow: Boolean

    /**
     * Speaks [text] and returns when it is finished. If no voice is available this must still take
     * about as long as speaking would, so animations stay paced the same way.
     */
    suspend fun say(text: String)

    fun stop()
}

interface Sfx {
    fun tick()
    fun correct()
    fun wrong()
    fun star()
    fun fanfare()
}

/** Tiny persistent settings/progress store (SharedPreferences on the TV). */
interface KeyValueStore {
    fun getInt(key: String, default: Int): Int
    fun putInt(key: String, value: Int)
    fun getBool(key: String, default: Boolean): Boolean
    fun putBool(key: String, value: Boolean)
    fun getString(key: String, default: String): String
    fun putString(key: String, value: String)
    fun clearAll()
}

/** Reads bundled text files (the question banks under assets/seed/). */
interface AssetReader {
    fun read(path: String): String
}

class Platform(
    val narrator: Narrator,
    val sfx: Sfx,
    val store: KeyValueStore,
    val assets: AssetReader,
)

/** Rough time to say [text] aloud (or to read it, when the voice is off), in milliseconds. */
fun speakingTimeMs(text: String): Long = 700L + text.length * 62L
