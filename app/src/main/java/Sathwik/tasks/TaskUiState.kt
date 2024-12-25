package Sathwik.tasks

import Sathwik.tasks.data.Task

data class TaskUiState(
    val tasks: List<Task> = listOf(Task("1","Eat banana")),
)