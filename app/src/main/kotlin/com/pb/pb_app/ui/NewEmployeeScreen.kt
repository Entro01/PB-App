package com.pb.pb_app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RememberMe
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pb.pb_app.data.enums.EmployeeRole
import com.pb.pb_app.ui.reusables.PBMediumTopBar
import com.pb.pb_app.ui.reusables.SingleLineFormField
import com.pb.pb_app.viewmodels.NewEmployeeViewModel


@Preview
@Composable
fun NewEmployeeScreen(navController: NavController = rememberNavController()) {
    val viewModel: NewEmployeeViewModel = viewModel(factory = NewEmployeeViewModel.factory)
    var shouldExpandRolePicker by remember {
        mutableStateOf(false)
    }

    val isSaveButtonEnabled by viewModel.shouldEnableSaveButton.collectAsState()


    Scaffold(
        Modifier.fillMaxSize(),
        topBar = { PBMediumTopBar("New Inquiry", isSaveButtonEnabled) { viewModel.createNewEmployee(); navController.navigateUp() } }) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(vertical = paddingValues.calculateTopPadding())
                .padding(bottom = 6.dp, start = 6.dp, end = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val rowModifier = Modifier.padding(8.dp, 4.dp)
            val arrangement = Arrangement.spacedBy(8.dp)
            val iconModifier = Modifier
                .padding(8.dp)
                .size(32.dp)

            Row(rowModifier, arrangement) {
                Icon(Icons.Default.RememberMe, modifier = iconModifier, contentDescription = "")
                SingleLineFormField(modifier = Modifier.fillMaxWidth(), onTextChange = { viewModel.updateNewEmployee(employeeId = it) }, placeholder = "Employee ID")
            }

            EmployeeRoleChipList {
                viewModel.updateNewEmployee(role = it)
            }

            Row(rowModifier, arrangement) {
                Icon(Icons.Default.Person, modifier = iconModifier, contentDescription = "new employee name icon")
                SingleLineFormField(
                    modifier = Modifier.fillMaxWidth(), onTextChange = { viewModel.updateNewEmployee(name = it) }, placeholder = "Name"
                )
            }

            Row(rowModifier, arrangement) {
                Icon(Icons.Default.Phone, modifier = iconModifier, contentDescription = "new employee phone number")
                SingleLineFormField(modifier = Modifier.fillMaxWidth(), onTextChange = { viewModel.updateNewEmployee(contactNumber = it) }, placeholder = "Phone number")
            }

            Row(rowModifier, arrangement) {
                Icon(Icons.Default.Email, modifier = iconModifier, contentDescription = "")
                SingleLineFormField(modifier = Modifier.fillMaxWidth(), onTextChange = { viewModel.updateNewEmployee(emailAddress = it) }, placeholder = "Email Address")
            }
        }
    }
}


@Composable
fun EmployeeRoleChipList(onServiceSelected: (EmployeeRole) -> Unit) {
    var selectedIndex by remember {
        mutableIntStateOf(2)
    }

    LazyRow(Modifier.padding(4.dp), contentPadding = PaddingValues(4.dp)) {
        itemsIndexed(EmployeeRole.entries.toList()) { index, item ->
            FilterChip(
                modifier = Modifier.padding(horizontal = 8.dp),
                selected = selectedIndex == index,
                onClick = { selectedIndex = index; onServiceSelected(item) },
                label = { Text(text = item.name) },
            )
        }
    }
}