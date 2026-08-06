package com.gumthala.learningapp.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gumthala.learningapp.domain.model.AppLanguage
import com.gumthala.learningapp.domain.model.Role
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "session")

data class UserSession(
    val userId: String,
    val role: Role,
    val displayName: String,
    val classLevel: Int?
)

/**
 * Persists the logged-in user until they explicitly tap Logout, per the app's
 * "stay logged in" requirement. Backed by DataStore so it survives process death.
 */
class SessionManager(private val context: Context) {

    private object Keys {
        val userId = stringPreferencesKey("user_id")
        val role = stringPreferencesKey("role")
        val displayName = stringPreferencesKey("display_name")
        val classLevel = intPreferencesKey("class_level")
        val language = stringPreferencesKey("language")
    }

    val session: Flow<UserSession?> = context.sessionDataStore.data.map { prefs ->
        val role = prefs[Keys.role]?.let { runCatching { Role.valueOf(it) }.getOrNull() }
        val userId = prefs[Keys.userId]
        if (role == null || userId == null) return@map null
        UserSession(
            userId = userId,
            role = role,
            displayName = prefs[Keys.displayName].orEmpty(),
            classLevel = prefs[Keys.classLevel]
        )
    }

    val language: Flow<AppLanguage> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.language]?.let { code -> AppLanguage.entries.find { it.code == code } }
            ?: AppLanguage.ENGLISH
    }

    suspend fun signIn(userId: String, role: Role, displayName: String, classLevel: Int? = null) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.userId] = userId
            prefs[Keys.role] = role.name
            prefs[Keys.displayName] = displayName
            if (classLevel != null) {
                prefs[Keys.classLevel] = classLevel
            } else {
                prefs.remove(Keys.classLevel)
            }
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.sessionDataStore.edit { prefs -> prefs[Keys.language] = language.code }
    }

    suspend fun logout() {
        context.sessionDataStore.edit { prefs ->
            prefs.remove(Keys.userId)
            prefs.remove(Keys.role)
            prefs.remove(Keys.displayName)
            prefs.remove(Keys.classLevel)
        }
    }
}
