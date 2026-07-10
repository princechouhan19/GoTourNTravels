package com.gotourntravels.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gotourntravels.models.User
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("gotour_prefs")

@Singleton
class UserPrefs @Inject constructor(@ApplicationContext private val ctx: Context) {

    private val gson = Gson()

    val token: Flow<String?> = ctx.dataStore.data.map { it[KEY_TOKEN] }
    val user: Flow<User?> = ctx.dataStore.data.map {
        it[KEY_USER]?.let { json -> runCatching { gson.fromJson(json, User::class.java) }.getOrNull() }
    }
    val darkMode: Flow<Boolean> = ctx.dataStore.data.map { it[KEY_DARK] ?: false }
    val onboardingDone: Flow<Boolean> = ctx.dataStore.data.map { it[KEY_ONBOARDING] ?: false }

    suspend fun saveAuth(token: String, user: User) {
        ctx.dataStore.edit {
            it[KEY_TOKEN] = token
            it[KEY_USER] = gson.toJson(user)
        }
    }

    suspend fun updateUser(user: User) {
        ctx.dataStore.edit { it[KEY_USER] = gson.toJson(user) }
    }

    suspend fun setDarkMode(value: Boolean) {
        ctx.dataStore.edit { it[KEY_DARK] = value }
    }

    suspend fun setOnboardingDone(value: Boolean) {
        ctx.dataStore.edit { it[KEY_ONBOARDING] = value }
    }

    suspend fun clear() {
        ctx.dataStore.edit { it.remove(KEY_TOKEN); it.remove(KEY_USER) }
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_USER = stringPreferencesKey("user")
        private val KEY_DARK = booleanPreferencesKey("dark_mode")
        private val KEY_ONBOARDING = booleanPreferencesKey("onboarding_done")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun provideUserPrefs(@ApplicationContext ctx: Context): UserPrefs = UserPrefs(ctx)
}
