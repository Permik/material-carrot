package xyz.santtu.materialcarrot

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import java.security.SecureRandom

class AddProfileFragmentViewModel() : ViewModel() {
    private val prng: SecureRandom by lazy { SecureRandom.getInstance("SHA1PRNG") }
    private val profileSecret = MutableLiveData<ByteArray>()

    fun getProfileSecret(): MutableLiveData<ByteArray>{
        return if (profileSecret.value == null){
            val randomBytes = ByteArray(8)
            prng.nextBytes(randomBytes)
            profileSecret.value = randomBytes
            return profileSecret
        }else{
            profileSecret
        }
    }
}