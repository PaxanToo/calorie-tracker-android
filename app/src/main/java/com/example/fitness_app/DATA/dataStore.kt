package com.example.fitness_app.DATA

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore by preferencesDataStore(name = "calories_prefs")

object PrefsKeys {
    val CAL_GOAL = intPreferencesKey("cal_goal")
    val CAL_EATEN = intPreferencesKey("cal_eaten")
    val ACH_GOAL_REACHED = booleanPreferencesKey("ach_goal_reached")
    val CAL_ENTRIES = stringPreferencesKey("cal_entries")
}

fun Context.prefsDataStore() = dataStore