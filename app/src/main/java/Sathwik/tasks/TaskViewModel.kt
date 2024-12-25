package Sathwik.tasks

import Sathwik.tasks.data.Task
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TaskViewModel : ViewModel(){
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState

    // Functions of the tasks application

    // 1.Adding a task  (W)
    fun addTask(task: Task){
        _uiState.update{ currentState ->
            currentState.copy(
                tasks = currentState.tasks+task
            )
        }
    }

    // 2.Modifying a task (F)
    fun modifyTask(id: String, name: String) {
        _uiState.update { currentState ->
            val task = currentState.tasks.find { it.id.equals(id) } // Use it.id for comparison
            if (task != null) {
                task.name = name // Modifying the task directly
            }
            currentState // Return the current state (no need to copy)
        }
    }

    // 3.Deleting a task (W)
    fun deleteTask(id: String){
        _uiState.update{
            currentState->
           val task =  currentState.tasks.find{id.equals(id)}
            if(task!=null)
            currentState.copy(
                tasks = currentState.tasks-task
            )else{
                currentState.copy(
                    tasks = currentState.tasks
                )
            }
        }
    }

}