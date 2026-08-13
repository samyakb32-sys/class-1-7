package com.gumthala.learningapp.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Tracks which bundled-content version has been seeded into Room.
 *
 * Deliberately a separate DataStore file from the session one: logging out
 * clears the session, and re-running the whole content seed on every logout
 * would be wasteful and surprising. Content versioning outlives sign-in.
 *
 * See SeedLoader.CONTENT_VERSION.
 */
internal val Context.seedStore: DataStore<Preferences> by preferencesDataStore("seed")
