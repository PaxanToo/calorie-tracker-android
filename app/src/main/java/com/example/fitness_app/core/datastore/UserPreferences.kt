package com.example.fitness_app.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import com.example.fitness_app.domain.model.ActivityLevel
import com.example.fitness_app.domain.model.AgeGroup
import com.example.fitness_app.domain.model.Gender
import com.example.fitness_app.domain.model.Goal
import com.example.fitness_app.domain.model.HeightGroup
import com.example.fitness_app.domain.model.WeightGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.fitness_app.core.datastore.decodeDailyProgressList

private val Context.dataStore by preferencesDataStore(name = "calories_prefs")


data class UserProfileData(
    val gender: Gender,
    val age: AgeGroup,
    val height: HeightGroup,
    val weight: WeightGroup,
    val activity: ActivityLevel,
    val goal: Goal,
    val calories: Int,
    val proteins: Int,
    val fats: Int,
    val carbs: Int
)

object PrefsKeys {
    val CAL_GOAL = intPreferencesKey("cal_goal")
    val CAL_EATEN = intPreferencesKey("cal_eaten")
    val ACH_GOAL_REACHED = booleanPreferencesKey("ach_goal_reached")
    val CAL_ENTRIES = stringPreferencesKey("cal_entries")
    val HAS_PROFILE = booleanPreferencesKey("has_profile")
    val PROFILE_GENDER = stringPreferencesKey("profile_gender")
    val PROFILE_AGE = stringPreferencesKey("profile_age")
    val PROFILE_HEIGHT = stringPreferencesKey("profile_height")
    val PROFILE_WEIGHT = stringPreferencesKey("profile_weight")
    val PROFILE_ACTIVITY = stringPreferencesKey("profile_activity")
    val PROFILE_GOAL = stringPreferencesKey("profile_goal")
    val PROFILE_CALORIES = intPreferencesKey("profile_calories")
    val PROFILE_PROTEINS = intPreferencesKey("profile_proteins")
    val PROFILE_FATS = intPreferencesKey("profile_fats")
    val PROFILE_CARBS = intPreferencesKey("profile_carbs")
    val DAILY_PROGRESS_HISTORY = stringPreferencesKey("daily_progress_history")
    val LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
    val PROTEIN_EATEN = intPreferencesKey("protein_eaten")
    val FAT_EATEN = intPreferencesKey("fat_eaten")
    val CARBS_EATEN = intPreferencesKey("carbs_eaten")
}

fun Context.prefsDataStore() = dataStore

fun Context.hasProfileFlow(): Flow<Boolean> {
    return dataStore.data.map { preferences ->
        preferences[PrefsKeys.HAS_PROFILE] ?: false
    }
}

fun Context.userProfileFlow(): Flow<UserProfileData?> {
    return dataStore.data.map { preferences ->
        val hasProfile = preferences[PrefsKeys.HAS_PROFILE] ?: false
        if (!hasProfile) return@map null

        val gender = preferences[PrefsKeys.PROFILE_GENDER]
            ?.let { enumValueOrNull<Gender>(it) } ?: return@map null

        val age = preferences[PrefsKeys.PROFILE_AGE]
            ?.let { enumValueOrNull<AgeGroup>(it) } ?: return@map null

        val height = preferences[PrefsKeys.PROFILE_HEIGHT]
            ?.let { enumValueOrNull<HeightGroup>(it) } ?: return@map null

        val weight = preferences[PrefsKeys.PROFILE_WEIGHT]
            ?.let { enumValueOrNull<WeightGroup>(it) } ?: return@map null

        val activity = preferences[PrefsKeys.PROFILE_ACTIVITY]
            ?.let { enumValueOrNull<ActivityLevel>(it) } ?: return@map null

        val goal = preferences[PrefsKeys.PROFILE_GOAL]
            ?.let { enumValueOrNull<Goal>(it) } ?: return@map null

        UserProfileData(
            gender = gender,
            age = age,
            height = height,
            weight = weight,
            activity = activity,
            goal = goal,
            calories = preferences[PrefsKeys.PROFILE_CALORIES] ?: 0,
            proteins = preferences[PrefsKeys.PROFILE_PROTEINS] ?: 0,
            fats = preferences[PrefsKeys.PROFILE_FATS] ?: 0,
            carbs = preferences[PrefsKeys.PROFILE_CARBS] ?: 0
        )
    }
}

suspend fun Context.saveUserProfile(profile: UserProfileData) {
    dataStore.edit { preferences ->
        preferences[PrefsKeys.HAS_PROFILE] = true

        preferences[PrefsKeys.PROFILE_GENDER] = profile.gender.name
        preferences[PrefsKeys.PROFILE_AGE] = profile.age.name
        preferences[PrefsKeys.PROFILE_HEIGHT] = profile.height.name
        preferences[PrefsKeys.PROFILE_WEIGHT] = profile.weight.name
        preferences[PrefsKeys.PROFILE_ACTIVITY] = profile.activity.name
        preferences[PrefsKeys.PROFILE_GOAL] = profile.goal.name

        preferences[PrefsKeys.PROFILE_CALORIES] = profile.calories
        preferences[PrefsKeys.PROFILE_PROTEINS] = profile.proteins
        preferences[PrefsKeys.PROFILE_FATS] = profile.fats
        preferences[PrefsKeys.PROFILE_CARBS] = profile.carbs

        // Чтобы главный экран использовал новую цель по калориям
        preferences[PrefsKeys.CAL_GOAL] = profile.calories
    }
}

suspend fun Context.clearUserProfile() {
    dataStore.edit { preferences ->
        preferences.remove(PrefsKeys.HAS_PROFILE)
        preferences.remove(PrefsKeys.PROFILE_GENDER)
        preferences.remove(PrefsKeys.PROFILE_AGE)
        preferences.remove(PrefsKeys.PROFILE_HEIGHT)
        preferences.remove(PrefsKeys.PROFILE_WEIGHT)
        preferences.remove(PrefsKeys.PROFILE_ACTIVITY)
        preferences.remove(PrefsKeys.PROFILE_GOAL)
        preferences.remove(PrefsKeys.PROFILE_CALORIES)
        preferences.remove(PrefsKeys.PROFILE_PROTEINS)
        preferences.remove(PrefsKeys.PROFILE_FATS)
        preferences.remove(PrefsKeys.PROFILE_CARBS)
    }
}

private inline fun <reified T : Enum<T>> enumValueOrNull(value: String): T? {
    return runCatching { enumValueOf<T>(value) }.getOrNull()
}


fun Context.dailyProgressHistoryFlow(): Flow<List<DailyProgress>> {
    return dataStore.data.map { preferences ->
        decodeDailyProgressList(
            preferences[PrefsKeys.DAILY_PROGRESS_HISTORY] ?: ""
        )
    }
}