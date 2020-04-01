package xyz.santtu.materialcarrot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.reflect.KProperty


const val OTP_PASSWORD_KEY = "otpPasswordKey"
const val COUNTDOWN_START_KEY = "countdownStartKey"
const val SELECTED_PROFILE_KEY = "selectedProfileKey"
const val PROFILE_LIST_KEY = "profileListKey"
const val UTC_OFFSET_KEY = "utcOffsetKey"
class MainScreenViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val state = savedStateHandle



//    var onetimePassword: MutableLiveData<String>

        fun getOnetimePassword(): MutableLiveData<String> = state.getLiveData(OTP_PASSWORD_KEY, "")
        fun setOnetimePassword(value: String): Unit = state.set(OTP_PASSWORD_KEY, value)



        //    var countdownStart: MutableLiveData<Long>
        fun getCountdownStart(): MutableLiveData<Long> = state.getLiveData(COUNTDOWN_START_KEY, 0)
        fun setCountdownStart(value: Long): Unit  = state.set(COUNTDOWN_START_KEY, value)

//    var selectedProfile: MutableLiveData<Int>
        fun getSelectedProfile(): MutableLiveData<Int> = state.getLiveData(SELECTED_PROFILE_KEY, 0)
        fun setSelectedProfile(value: Int): Unit  = state.set(SELECTED_PROFILE_KEY, value)

//    var profileList: MutableLiveData<TreeMap<String, String>>
        fun getProfileList(): MutableLiveData<TreeMap<String, String>> {
            return state.getLiveData(PROFILE_LIST_KEY, TreeMap<String, String>())
        }
        fun setProfileList(value: TreeMap<String, String> ): Unit = state.set(PROFILE_LIST_KEY, value)

        fun getUtcOffset(): LiveData<String> {
            val offset: LiveData<String> = state.getLiveData(UTC_OFFSET_KEY)
            return if (offset.value.isNullOrEmpty()){
                state.set(UTC_OFFSET_KEY, UtcOffset())
                offset
            }else{
                offset
            }
        }
}