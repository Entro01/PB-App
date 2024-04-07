package com.pb.pb_app.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

private const val TAG = "NewEnquiryScreen"


//    @Composable
//    fun NewEnquiryScreen() {
//        val newEnquiry = viewModel.newEnquiry.collectAsState().value
//        Surface(color = MaterialTheme.colorScheme.surfaceContainer, shape = MaterialTheme.shapes.medium, modifier = Modifier.padding(8.dp)) {
//            Column(
//                Modifier
//                    .fillMaxSize()
//                    .padding(8.dp),
//                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                TextField(value = newEnquiry.name, onValueChange = {
//                    viewModel.updateNewEnquiry(name = it)
//                }, placeholder = { Text(text = "Title") }, modifier = Modifier.fillMaxWidth())
//
//                OutlinedTextField(modifier = Modifier
//                    .weight(1F)
//                    .fillMaxWidth(), value = newEnquiry.description, onValueChange = {
//                    viewModel.updateNewEnquiry(description = it)
//                    Log.e(TAG, "NewEnquiryScreen: ${viewModel.newEnquiry.value?.description}")
//                }, placeholder = { Text(text = "Description") })
//            }
//        }
//    }
