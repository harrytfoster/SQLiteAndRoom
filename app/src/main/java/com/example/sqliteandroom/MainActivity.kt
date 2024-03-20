package com.example.sqliteandroom

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sqliteandroom.ui.theme.SQLiteAndRoomTheme
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Database(entities = arrayOf(Song::class), version = 1, exportSchema = false)
public abstract class SongDatabase: RoomDatabase() {
    abstract fun SongDao(): SongDao

    companion object {
        private var instance: SongDatabase? = null

        fun getDatabase(ctx: Context) : SongDatabase {
            var tmpInstance = instance
            if(tmpInstance == null) {
                tmpInstance = Room.databaseBuilder(
                    ctx.applicationContext,
                    SongDatabase::class.java,
                    "SongDatabase"
                ).build()
                instance = tmpInstance
            }
            return tmpInstance
        }
    }
}

@Entity(tableName="Songs")

data class Song(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val title: String,
    var artist: String, var year: String
)

@Dao
interface SongDao {

    @Query("SELECT * FROM Songs WHERE id=:id")
    fun getSongbyID(id: Long): Song?

    @Query("SELECT * FROM Songs")
    fun getAllSongs(): List<Song>

    @Insert
    fun insert(Song: Song) : Long

    @Update
    fun update(Song: Song) : Int

    @Delete
    fun delete(Song: Song) : Int
}

class MainActivity : ComponentActivity() {
    lateinit var db: SongDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = SongDatabase.getDatabase(application)

        setContent {
            Button(onClick = {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO){
                        val s = Song(id=1, title="Much Better Off", artist="Smokey Robinson And The Miracles", year="1965")
                        db.SongDao().insert(s)
                    }
                }
            }) {
              Text("Add Song")
            }
        }
    }
}