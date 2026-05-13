package com.example.finalproject.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore("clarity_settings")

class SettingsStore(private val context: Context) {
    val primaryColorHex: Flow<String> = context.settingsDataStore.data.map { prefs ->
        prefs[PRIMARY_COLOR] ?: "#3E6658"
    }

    suspend fun setPrimaryColor(hex: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[PRIMARY_COLOR] = hex
        }
    }

    companion object {
        private val PRIMARY_COLOR = stringPreferencesKey("primary_color")
    }
}
