package xyz.santtu.materialcarrotrepository

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProfileDao {

    @Query("SELECT * from profile_table")
    fun observeProfiles(): LiveData<List<Profile>>

    @Query("SELECT * from profile_table")
    fun getProfiles(): List<Profile>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Profile)

    @Query("DELETE FROM profile_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(profile: Profile)
}