package Sathwik.tasks.ui

import Sathwik.tasks.R
import Sathwik.tasks.data.Task
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel


import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// just displays each taskItem.
// has nothing to do with the display and editing of the task.
@Composable
fun TaskDisplay(
    task:Task,
    editTask: (Int) -> Unit,
    deleteTask: (Int) -> Unit,
    modifier: Modifier = Modifier,
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(16.dp)) // Reduced corner radius
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Added card color
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp), // Use spacedBy for consistent spacing
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Task ID with icon
            Icon(
                imageVector = Icons.Filled.DateRange, // Added an icon
                contentDescription = "Task ID",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = task.id.toString(),
                style = MaterialTheme.typography.bodyMedium, // Smaller font size
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Task Name with emphasis
            Text(
                text = task.name,
                style = MaterialTheme.typography.titleMedium, // Larger font size and weight
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.weight(1f)) // Push buttons to the end

            // Edit Button
            IconButton(onClick = { editTask(task.id) }) {
                Icon(
                    imageVector = Icons.Filled.Edit, // More descriptive icon
                    contentDescription = "Edit Task",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Delete Button
            IconButton(onClick = { deleteTask(task.id) }) {
                Icon(
                    imageVector = Icons.Filled.Delete, // More descriptive icon
                    contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.error // Error color for delete
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    item: Task,
    editTask: (Int) -> Unit,
    deleteTask: (Int) -> Unit,
){
        Row {
                TaskDisplay(
                    item,
                    editTask = editTask,
                    deleteTask = deleteTask
                )
            }
    }

//the top Bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskAppBar(
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text("Tasks") },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
    )
}

//display the taskItems
@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
//    uiState: TaskUiState,
    editTask: (Int) -> Unit,
    deleteTask: (Int) -> Unit,
    tasks: List<Task>,

    ) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) //here this lazy column takes the entire screen by filling max size
        ){
            items(tasks){
                    item ->//getting item form sItems
                    TaskItem(
                        item,
                        editTask = editTask,
                       deleteTask = deleteTask
                    )
            }
        }
    }
}


//the starting point of the application
@Preview(showBackground = true)
@Composable
fun TasksApp(
    taskViewModel: TaskViewModel = viewModel(factory = TaskViewModel.factory), // now got a view model with database connectivity..
){

    // this is a instance variable to display the input dialog
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var currentEditItemId by remember { mutableStateOf(1)}

    // used to read input into the input dialog
    var taskName by remember { mutableStateOf("") }
//    var taskId by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    //val taskUiState = taskViewModel.taskUiState

    val allTasks by taskViewModel.getAllTasks().collectAsState(emptyList())
    //println(allTasks)

    fun editTask(id: Int):Unit{
        currentEditItemId = id
        showEditDialog = true
    }

    fun deleteTask(id: Int):Unit{
        currentEditItemId = id
        var task: Task
            coroutineScope.launch{
               task =  taskViewModel.getTask(currentEditItemId).first()
                taskViewModel.deleteTask(task)
            }


    }

    Scaffold(
        topBar = {
            TaskAppBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                },
                contentColor = Color.Black // Customize icon color
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding->
        StartScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            tasks = allTasks,
            editTask = ::editTask,
            deleteTask = ::deleteTask
        )
        //this just adds a task
        if(showAddDialog==true){
            AlertDialog(onDismissRequest = {
                showAddDialog=false
                taskName = "" },
                confirmButton = {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ){
                        //confirm button
                        Button(onClick = {
                            coroutineScope.launch {
                                taskViewModel.saveTask()
                                taskName = ""
                                showAddDialog = false
                            }
                        }){
                            Text("Add")
                        }
                        //cancel button
                        Button(onClick = {
                            showAddDialog= false
                            taskName = ""
                        }){
                            Text("Cancel")
                        }
                    }
                },
                title = { Text("Add a Task") },
                text={
                    //here are going to misuse text is a composable that it accepts
                    //so we use all kind of composables other than text
                    Column{
                        OutlinedTextField(
                            //the input text field used to take input
                            value = taskName,
                            onValueChange = {newValue->
                                taskName = newValue
                                val newTaskDetails = TaskDetails(name = taskName)
                                taskViewModel.updateUiState(newTaskDetails)
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            )
        }

        if(showEditDialog == true){
            AlertDialog(onDismissRequest = {
                showEditDialog=false
                taskName = "" },
                confirmButton = {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ){
                        Button(onClick = {
                            coroutineScope.launch{
                                taskViewModel.modifyTask(currentEditItemId,taskName)
                                taskName = ""
                                showEditDialog = false
                            }

                        }){
                            Text("Modify")
                        }
                        Button(onClick = {
                            showEditDialog= false
//                            taskId = ""
                            taskName = ""
                        }){
                            Text("Cancel")
                        }
                    }
                },
                title = { Text("Edit a Task") },
                text= {
                    //here are going to misuse text is a composable that it accepts
                    //so we use all kind of composables other than text
                    Column{
                        OutlinedTextField(
                            value = taskName,
                            onValueChange = {taskName = it},
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            )
        }
    }
}