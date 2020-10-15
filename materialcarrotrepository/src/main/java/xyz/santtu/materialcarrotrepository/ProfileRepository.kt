package xyz.santtu.materialcarrotrepository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow

/**
 * Repository of profiles.
 *
 * Interfaces [ProfileDao], that use [androidx.room] library via
 * [ProfileRoomDatabase] to manipulate [Profile]s in a SQLite DB
 */
class ProfileRepository(private val profileDao: ProfileDao){

    /**
     * Gets a list in a Livedata that room updates whenever there's a change in the database
     * @return [LiveData] that contains a [List] of [Profile]
     */
    fun allProfiles(): Flow<List<Profile>> {
        return profileDao.observeProfiles()
    }

    /**
     * Suspend function that inserts a [Profile] to [ProfileRoomDatabase]
     * @param profile [Profile] that'll be inserted to the [ProfileRoomDatabase]
     */
    suspend fun insert(profile: Profile){
        profileDao.insert(profile)
    }

    /**
     * Suspend function that deletes a [Profile] from [ProfileRoomDatabase]
     * @param profile [Profile] that'll be deleted from [ProfileRoomDatabase]
     */
    suspend fun delete(profile: Profile){
        profileDao.delete(profile)
    }

}