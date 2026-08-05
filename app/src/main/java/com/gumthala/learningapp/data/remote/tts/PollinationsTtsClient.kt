package com.gumthala.learningapp.data.remote.tts

import android.content.Context
import com.gumthala.learningapp.domain.model.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.security.MessageDigest

/**
 * Fetches question audio from Pollinations AI's text-to-speech endpoint
 * (https://text.pollinations.ai/{text}?model=openai-audio&voice=...) and caches
 * it on disk so the same question doesn't re-download audio every time it's played.
 */
class PollinationsTtsClient(
    private val context: Context,
    private val client: OkHttpClient = OkHttpClient()
) {
    private val cacheDir: File by lazy {
        File(context.cacheDir, "tts").apply { mkdirs() }
    }

    /** Returns the cached mp3 file for [text] in [language], downloading it first if needed. */
    suspend fun fetchAudio(text: String, language: AppLanguage): Result<File> = withContext(Dispatchers.IO) {
        runCatching {
            val cacheFile = File(cacheDir, cacheKey(text, language))
            if (cacheFile.exists() && cacheFile.length() > 0) return@runCatching cacheFile

            val url = HttpUrl.Builder()
                .scheme("https")
                .host("text.pollinations.ai")
                .addPathSegment(text)
                .addQueryParameter("model", "openai-audio")
                .addQueryParameter("voice", voiceFor(language))
                .build()

            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) error("TTS request failed: ${response.code}")
                val bytes = response.body?.bytes() ?: error("Empty TTS response")
                cacheFile.writeBytes(bytes)
            }
            cacheFile
        }
    }

    private fun voiceFor(language: AppLanguage): String = when (language) {
        AppLanguage.ENGLISH -> "nova"
        AppLanguage.MARATHI -> "alloy"
        AppLanguage.HINDI -> "alloy"
    }

    private fun cacheKey(text: String, language: AppLanguage): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest("${language.code}:$text".toByteArray())
        val hex = digest.joinToString("") { "%02x".format(it) }
        return "$hex.mp3"
    }
}
