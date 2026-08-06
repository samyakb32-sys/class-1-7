package com.gumthala.learningapp.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gumthala.learningapp.core.AppLanguage
import com.gumthala.learningapp.core.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionStore: DataStore<Preferences> by preferencesDataStore("session")

data class Session(
    val userId: String,
    val role: UserRole,
    val displayName: String,
    val classLevel: Int?
)

/**
 * Persistent session — survives process death and reboots. Cleared only by an
 * explicit Logout, per spec.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val ROLE = stringPreferencesKey("role")
        val NAME = stringPreferencesKey("display_name")
        val CLASS_LEVEL = intPreferencesKey("class_level")
        val LANGUAGE = stringPreferencesKey("language")
    }

    val session: Flow<Session?> = context.sessionStore.data.map { prefs ->
        val id = prefs[Keys.USER_ID] ?: return@map null
        val role = prefs[Keys.ROLE]?.let { runCatching { UserRole.valueOf(it) }.getOrNull() } ?: return@map null
        Session(
            userId = id,
            role = role,
            displayName = prefs[Keys.NAME].orEmpty(),
            classLevel = prefs[Keys.CLASS_LEVEL]?.takeIf { it > 0 }
        )
    }

    val language: Flow<AppLanguage> = context.sessionStore.data
        .map { AppLanguage.fromCode(it[Keys.LANGUAGE]) }

    suspend fun current(): Session? = session.first()

    suspend fun save(session: Session) {
        context.sessionStore.edit { prefs ->
            prefs[Keys.USER_ID] = session.userId
            prefs[Keys.ROLE] = session.role.name
            prefs[Keys.NAME] = session.displayName
            prefs[Keys.CLASS_LEVEL] = session.classLevel ?: 0
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.sessionStore.edit { it[Keys.LANGUAGE] = language.code }
    }

    /** Logout. Language preference is intentionally kept. */
    suspend fun clear() {
        context.sessionStore.edit { prefs ->
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.ROLE)
            prefs.remove(Keys.NAME)
            prefs.remove(Keys.CLASS_LEVEL)
        }
    }
}
