package xyz.santtu.materialcarrotrepository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.santtu.materialcarrotutils.hexStringToByteArray

@Database(entities = [Profile::class], version = 1, exportSchema = false)
abstract class ProfileRoomDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao



    private class ProfileDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { profileRoomDatabase ->
                scope.launch {
                    populateDatabase(profileRoomDatabase.profileDao())
                }
            }
        }

        suspend fun populateDatabase(profileDao: ProfileDao){

            profileDao.deleteAll()

            var profile = Profile(0, "Test", "ffffffffffffffff".hexStringToByteArray())
            profileDao.insert(profile)
            profile = Profile(0, "Testyyyy", "fffffffffffffeee".hexStringToByteArray())
            profileDao.insert(profile)
        }
    }

    companion object{

        @Volatile
        private var INSTANCE: ProfileRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ProfileRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProfileRoomDatabase::class.java,
                    "profile_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}