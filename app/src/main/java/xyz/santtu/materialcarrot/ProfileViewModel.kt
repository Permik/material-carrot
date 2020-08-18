package xyz.santtu.materialcarrot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application): AndroidViewModel(application){
    private val repository: ProfileRepository

    val allProfiles: LiveData<List<Profile>>

    init {
        val profilesDao = ProfileRoomDatabase.getDatabase(application, viewModelScope).profileDao()
        repository = ProfileRepository(profilesDao)
        allProfiles = repository.allProfiles()
    }

    fun insert(profile: Profile) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(profile)
    }

    fun delete(profile: Profile) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(profile)
    }

}