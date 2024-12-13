package com.example.driverattentiveness.data.pref

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
            preferences[NAME_KEY] = user.name
            preferences[ID_KEY] = user.id
            preferences[AGE_KEY] = user.age
            preferences[USER_TRIP_KEY] = user.tripId
        }
        Log.d("UserPreference", "Session saved: email=${user.email}, token=${user.token}, name=${user.name}, id=${user.id}, age=${user.age}")
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[IS_LOGIN_KEY] ?: false,
                preferences[NAME_KEY] ?: "",
                preferences[ID_KEY] ?: "",
                preferences[AGE_KEY] ?: 0,
                preferences[USER_TRIP_KEY] ?: ""
            )
        }
    }

    suspend fun saveTripId(tripId: String) {
        dataStore.edit { preferences ->
            preferences[USER_TRIP_KEY] = tripId
        }
        Log.d("UserPreference", "Trip ID saved: $tripId")
    }

    suspend fun incrementAlertCount() {
        dataStore.edit { preferences ->
            val currentCount = preferences[ALERT_COUNT_KEY] ?: 0
            preferences[ALERT_COUNT_KEY] = currentCount + 1
        }
        Log.d("UserPreference", "Alert count incremented")
    }

    fun getAlertCount(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[ALERT_COUNT_KEY] ?: 0
        }
    }

    suspend fun resetAlertCount() {
        dataStore.edit { preferences ->
            preferences[ALERT_COUNT_KEY] = 0
        }
        Log.d("UserPreference", "Alert count reset")
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val NAME_KEY = stringPreferencesKey("name")
        private val ID_KEY = stringPreferencesKey("id")
        private val AGE_KEY = intPreferencesKey("age")
        private val USER_TRIP_KEY = stringPreferencesKey("user_trip_key")
        private val ALERT_COUNT_KEY = intPreferencesKey("alert_count")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}