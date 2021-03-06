package xyz.santtu.materialcarrotrepository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application): AndroidViewModel(application){
    private lateinit var repository: ProfileRepository
    lateinit var allProfiles: LiveData<List<Profile>>

    init {
        viewModelScope.launch {
            val profilesDao = ProfileRoomDatabase.getDatabase(application, viewModelScope).profileDao()
            repository = ProfileRepository(profilesDao)
            allProfiles = repository.allProfiles().asLiveData()
        }
    }

    /**
     * Launches a coroutine that inserts a [Profile] to the [ProfileRoomDatabase]
     * @param profile [Profile] that is inserted to the [ProfileRoomDatabase]
     */
    fun insert(profile: Profile) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(profile)
    }

    /**
     * Launches a coroutine that deletes a [Profile] from the [ProfileRoomDatabase]
     * @param profile [Profile] that is deleted form [ProfileRoomDatabase]
     */
    fun delete(profile: Profile) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(profile)
    }

}