package Sathwik.tasks.ui

import Sathwik.tasks.TaskApplication
import Sathwik.tasks.data.Task
import Sathwik.tasks.data.TaskDao
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TaskViewModel(private val taskDao: TaskDao) : ViewModel(){

    // the viewModel consisits of all the weapons to maniputlate the uiState
    var taskUiState by mutableStateOf(TaskUiState())
        private set

    fun updateUiState(taskDetails: TaskDetails){
        taskUiState = TaskUiState(taskDetails = taskDetails,isEntryValid = validateInput(taskDetails))
    }

    private fun validateInput(uiState:TaskDetails = taskUiState.taskDetails): Boolean{
        return with (uiState){
            name.isNotBlank()
        }
    }

    suspend fun saveTask() {
        try {
            if (validateInput()) {
                taskDao.insertTask(taskUiState.taskDetails.toTask())
            }
        } catch (e: Exception) {
            // Handle exception, e.g., log it or update UI state
        }
    }

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    fun getTask( id: Int): Flow<Task> = taskDao.getTask(id)

    companion object { //companion means static guys
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val taskApplication = (this[APPLICATION_KEY] as TaskApplication)
                TaskViewModel(taskApplication.database.taskDao())

            }
        }
    }

    // Functions of the tasks application

    // 1.Adding a task  (W)
//    fun addTask(task: Task){
//        _uiState.update{ currentState ->
//            currentState.copy(
//                tasks = currentState.tasks+task
//            )
//        }
//    }
//
    // 2.Modifying a task (F)
   suspend fun modifyTask(id: Int, name: String) {
       taskDao.updateTask(Task(id,name))
    }
//
//    // 3.Deleting a task (W)
   suspend fun deleteTask(task: Task){
        taskDao.deleteTask(task)
        }

}

data class TaskUiState(
    val taskDetails: TaskDetails = TaskDetails(),
    val isEntryValid: Boolean = false
)

data class TaskDetails(
    val id : Int = 1,
    val name: String = ""
)

fun TaskDetails.toTask(): Task = Task(
    name = name
)

fun Task.toTaskDetails(): TaskDetails = TaskDetails(
    id = id,
    name = name
)