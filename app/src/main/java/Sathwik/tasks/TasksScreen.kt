package Sathwik.tasks

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

// just displays each taskItem.
// has nothing to do with the display and editing of the task.
@Composable
fun TaskDisplay(
    task:Task,
    editTask: (String) -> Unit,
    deleteTask: (String) -> Unit,
    modifier: Modifier = Modifier,
){
    Card(
        modifier = Modifier
            .fillMaxWidth() // Card takes full width
            .wrapContentHeight() // Height adjusts to content
            .clip(RoundedCornerShape(32.dp))
            .padding(8.dp), // Rounded corners
    ){
        Row(
            modifier = modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //this is the id of the task
            Text(text=task.id)

            Spacer(modifier = Modifier.width(8.dp))

            Text(text = task.name) //this is the name of the task

            Spacer(modifier = Modifier.width(30.dp))

            Button(
                onClick = {
                    editTask(task.id)
                }
            ) {
                Image(
                    painter = painterResource(R.drawable.edit_24px),
                    contentDescription = null,
                )
            }
            Button(
                onClick = {
                    deleteTask(task.id)
                }
            ) {
                Image(
                    painter = painterResource(R.drawable.delete_24px),
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    item: Task,
    editTask: (String) -> Unit,
    deleteTask: (String) -> Unit
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
    uiState:TaskUiState,
    editTask: (String)->Unit,
    deleteTask: (String)->Unit
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
            items(uiState.tasks){
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
    taskViewModel: TaskViewModel = viewModel(),
){

    // state variable
    val uiState by taskViewModel.uiState.collectAsState()

    // this is a instance variable to display the input dialog
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var currentEditItemId by remember { mutableStateOf("")}

    // used to read input into the input dialog
    var taskName by remember { mutableStateOf("") }
    var taskId by remember { mutableStateOf("") }

    fun editTask(id: String):Unit{
        currentEditItemId = id
        showEditDialog = true
    }

    fun deleteTask(id: String):Unit{
        currentEditItemId = id
        taskViewModel.deleteTask(currentEditItemId)

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
            uiState = uiState,
            editTask = ::editTask ,// here we are passing the variable without parameters to the inside funtion the helps in abstracting the functioning
            deleteTask = ::deleteTask
        )
        //this just adds a task
        if(showAddDialog==true){
            AlertDialog(onDismissRequest = {
                showAddDialog=false
                taskId = ""
                taskName = "" },
                confirmButton = {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ){
                        Button(onClick = {
                            val newTask = Task(
                                id = taskId,
                                name = taskName,
                            )
                          taskViewModel.addTask(newTask)
                            taskId = ""
                            taskName = ""
                            showAddDialog = false
                        }){
                            Text("Add")
                        }
                        Button(onClick = {
                            showAddDialog= false
                            taskId = ""
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
                            value = taskId.toString(),
                            onValueChange = {taskId = it },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                        OutlinedTextField(
                            value = taskName,
                            onValueChange = {taskName = it },
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
                taskId = ""
                taskName = "" },
                confirmButton = {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ){
                        Button(onClick = {
                            taskViewModel.modifyTask(currentEditItemId,taskName)
                            taskId = ""
                            taskName = ""
                            showEditDialog = false
                        }){
                            Text("Modify")
                        }
                        Button(onClick = {
                            showEditDialog= false
                            taskId = ""
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
                            onValueChange = {taskName = it },
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