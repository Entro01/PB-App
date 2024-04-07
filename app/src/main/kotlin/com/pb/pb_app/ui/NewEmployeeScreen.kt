package com.pb.pb_app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

//    @Composable
//    fun NewEmployeeScreen() {
//
//
//        val newEmployee = viewModel.
//
//        Surface(color = MaterialTheme.colorScheme.surfaceContainer, shape = MaterialTheme.shapes.medium, modifier = Modifier.padding(8.dp)) {
//            Column(
//                Modifier
//                    .padding(8.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                TextField(value = newEmployee.name, onValueChange = {
//                    viewModel.updateNewEmployee(name = it)
//                }, placeholder = { Text(text = "Name") }, modifier = Modifier.fillMaxWidth())
//                TextField(value = newEmployee.emailAddress, onValueChange = {
//                    viewModel.updateNewEmployee(emailAddress = it)
//                }, placeholder = { Text(text = "Email") }, modifier = Modifier.fillMaxWidth())
//                TextField(value = newEmployee.contactNumber, onValueChange = {
//                    viewModel.updateNewEmployee(contactNumber = it)
//                }, placeholder = { Text(text = "Contact Number") }, modifier = Modifier.fillMaxWidth())
//            }
//        }
//    }
//
