package Sathwik.tasks

import Sathwik.tasks.data.TaskDatabase
import android.app.Application

class TaskApplication: Application() {
    val database: TaskDatabase by lazy{
        TaskDatabase.getDatabase(this)
    }
}