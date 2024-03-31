package com.pb.pb_app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pb.pb_app.MainViewModel

@Composable
fun FormScreen(viewModel: MainViewModel, navController: NavController) {
    var name: String by remember {
        mutableStateOf("")
    }

    var description: String by remember {
        mutableStateOf("")
    }

    Surface(Modifier.fillMaxSize(1F)) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(value = name, onValueChange = {
                name = it
            }, placeholder = { Text(text = "Title") })

            OutlinedTextField(modifier = Modifier.size(width = TextFieldDefaults.MinWidth, height = 400.dp), value = description, onValueChange = {
                description = it
            }, placeholder = { Text(text = "Description") })

            Button(enabled = name.isNotEmpty() && description.isNotEmpty(), onClick = { viewModel.createNewEnquiry(name, description) }) {
                Text("OK")
            }
        }
    }
}