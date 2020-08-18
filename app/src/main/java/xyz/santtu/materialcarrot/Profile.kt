package xyz.santtu.materialcarrot

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class Profile(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "profile_name") val profileName: String,
    @ColumnInfo(name = "profile_secret") val profileSecret: ByteArray
){

}