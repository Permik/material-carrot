package xyz.santtu.materialcarrot

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

data class MainState(
    val password: String,
    val pin: String,
    val countdownStart: Long,
    val selectedProfile: Int
)

private object StateKeys{
    val PASSWORD = preferencesKey<String>("password")
    val PIN = preferencesKey<String>("pin")
    val COUNTDOWN_START = preferencesKey<Long>("countdown_start")
    val SELECTED_PROFILE = preferencesKey<Int>("selected_profile")
}

class MainStateRepository(context: Context) {

    private val dataStore: DataStore<Preferences> = context.createDataStore("main_state")

    private val safeFlow: Flow<Preferences> = dataStore.data
        .catch { exeption ->
            if (exeption is IOException){
                emit(emptyPreferences())
            } else {
                throw exeption
            }
        }

    val MainStateFlow: Flow<MainState> = safeFlow
        .map { state ->
            val password = state[StateKeys.PASSWORD]?: ""
            val pin = state[StateKeys.PIN]?: ""
            val countdownStart = state[StateKeys.COUNTDOWN_START]?: 0
            val selectedProfile = state[StateKeys.SELECTED_PROFILE]?: 0
            MainState(password, pin, countdownStart, selectedProfile)
        }

    val passwordFlow: Flow<String> = safeFlow.map { state ->
        state[StateKeys.PASSWORD]?: ""
    }

    val pinFlow: Flow<String> = safeFlow.map { state ->
        state[StateKeys.PIN]?: ""
    }

    val countdownStartFlow: Flow<Long> = safeFlow.map { state ->
        state[StateKeys.COUNTDOWN_START]?: 0
    }

    val selectedProfileFlow: Flow<Int> = safeFlow.map { state ->
        state[StateKeys.SELECTED_PROFILE]?: 0
    }

    suspend fun updatePassword(password: String){
        dataStore.edit { state ->
            state[StateKeys.PASSWORD] = password
        }
    }

    suspend fun updatePin(pin: String){
        dataStore.edit { state ->
            state[StateKeys.PIN] = pin
        }
    }

    suspend fun updateCountdownStart(countdownStart: Long){
        dataStore.edit { state ->
            state[StateKeys.COUNTDOWN_START] = countdownStart
        }
    }

    suspend fun updateSelectedProfile(selectedProfile: Int){
        dataStore.edit { state ->
            state[StateKeys.SELECTED_PROFILE] = selectedProfile
        }
    }

    suspend fun updateMainState(mainState: MainState){
        updatePassword(mainState.password)
        updatePin(mainState.pin)
        updateCountdownStart(mainState.countdownStart)
        updateSelectedProfile(mainState.selectedProfile)
    }
}


