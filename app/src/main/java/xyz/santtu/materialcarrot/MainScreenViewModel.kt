package xyz.santtu.materialcarrot

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import xyz.santtu.materialcarrotutils.utcOffset



class MainScreenViewModel(application: Application) : AndroidViewModel(
    application
) {
    private lateinit var repository: MainStateRepository
    init {
        viewModelScope.launch {
            repository = MainStateRepository(application)
        }
    }
        fun setup() {
            viewModelScope.launch(Dispatchers.IO) {
                repository.MainStateFlow.take(1).collect {value ->  repository.updateMainState(value) }
            }
        }

        val mainState = repository.MainStateFlow.asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)

        fun getOnetimePassword(): LiveData<String> = repository.passwordFlow.asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
        fun setOnetimePassword(value: String) = viewModelScope.launch(Dispatchers.IO){
            repository.updatePassword(value)
        }

        val passwordPin: LiveData<String> = repository.pinFlow.asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
        fun setPasswordPin(value: String) = viewModelScope.launch(Dispatchers.IO){
            repository.updatePin(value)
        }

//    var countdownStart: MutableLiveData<Long>
        fun getCountdownStart(): LiveData<Long> = repository.countdownStartFlow.asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
        fun setCountdownStart(value: Long)= viewModelScope.launch(Dispatchers.IO){
            repository.updateCountdownStart(value)
        }

//    var selectedProfile: MutableLiveData<Int>
        val selectedProfile: LiveData<Int> = repository.selectedProfileFlow.asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
        fun setSelectedProfile(value: Int) = viewModelScope.launch(Dispatchers.IO){
            repository.updateSelectedProfile(value)
        }

        val utcOffset: LiveData<String> = liveData {
            emit(utcOffset())
        }
}
const val OTP_PASSWORD_KEY = "otpPasswordKey"
const val OTP_PASSWORD_PIN_KEY = "otpPasswordPinKey"
const val COUNTDOWN_START_KEY = "countdownStartKey"
const val SELECTED_PROFILE_KEY = "selectedProfileKey"
const val UTC_OFFSET_KEY = "utcOffsetKey"