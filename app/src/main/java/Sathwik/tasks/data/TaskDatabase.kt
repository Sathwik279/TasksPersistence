package Sathwik.tasks.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object { // this companion object is just like calling static methods in java
        @Volatile
        private var Instance: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance. this helps to model the singleton feature in kotlin.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, TaskDatabase::class.java, "todo_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}