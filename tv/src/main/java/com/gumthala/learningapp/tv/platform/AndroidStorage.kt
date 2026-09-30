package com.gumthala.learningapp.tv.platform

import android.content.Context
import com.gumthala.learningapp.tv.core.AssetReader
import com.gumthala.learningapp.tv.core.KeyValueStore

class AndroidStore(context: Context) : KeyValueStore {
    private val prefs = context.applicationContext.getSharedPreferences("maths_adventure_tv", Context.MODE_PRIVATE)

    override fun getInt(key: String, default: Int): Int = prefs.getInt(key, default)
    override fun putInt(key: String, value: Int) { prefs.edit().putInt(key, value).apply() }
    override fun getBool(key: String, default: Boolean): Boolean = prefs.getBoolean(key, default)
    override fun putBool(key: String, value: Boolean) { prefs.edit().putBoolean(key, value).apply() }
    override fun getString(key: String, default: String): String = prefs.getString(key, default) ?: default
    override fun putString(key: String, value: String) { prefs.edit().putString(key, value).apply() }
    override fun clearAll() { prefs.edit().clear().apply() }
}

class AndroidAssets(private val context: Context) : AssetReader {
    override fun read(path: String): String =
        context.assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
}
