package com.pb.pb_app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pb.pb_app.ui.viewmodels.NewEmployeeViewModel
import com.pb.pb_app.utils.models.employees.EmployeeRole
import com.pb.pb_app.utils.reusables.PBMediumTopBar
import com.pb.pb_app.utils.reusables.SingleLineFormField
import com.pb.pb_app.utils.reusables.emailAddressKeyboardOptions
import com.pb.pb_app.utils.reusables.phoneNumberKeyboardOptions


@Preview
@Composable
fun NewEmployeeScreen(navController: NavController = rememberNavController()) {
    val viewModel: NewEmployeeViewModel = viewModel(factory = NewEmployeeViewModel.factory)
    var shouldExpandRolePicker by remember {
        mutableStateOf(false)
    }

    val isSaveButtonEnabled by viewModel.shouldEnableSaveButton.collectAsState()


    Scaffold(Modifier.fillMaxSize(), topBar = { PBMediumTopBar("New Inquiry", isSaveButtonEnabled) { viewModel.createNewEmployee(); navController.navigateUp() } }) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(vertical = paddingValues.calculateTopPadding())
                .padding(bottom = 6.dp, start = 6.dp, end = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val rowArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            val rowAlignment = Alignment.CenterVertically
            val rowModifier = Modifier.fillMaxWidth()

            Row(rowModifier, rowArrangement, rowAlignment) {
                TextButton(onClick = { shouldExpandRolePicker = !shouldExpandRolePicker }) {
                    var roleText by remember {
                        mutableStateOf("Freelancer")
                    }

                    Text(roleText.ifBlank { "Pick Employee Type" })
                    EmployeeRolePicker(shouldExpandRolePicker, { shouldExpandRolePicker = false }) {
                        viewModel.updateNewEmployee(role = it); shouldExpandRolePicker = false; roleText = it.name.capitalize(Locale.current)
                    }
                }
            }
            Row(rowModifier, rowArrangement, rowAlignment) {
                SingleLineFormField(Modifier.weight(1F), { viewModel.updateNewEmployee(employeeId = it) }, "Employee ID")
                SingleLineFormField(Modifier.weight(1F), { viewModel.updateNewEmployee(name = it) }, "Name")
            }

            Row(rowModifier, rowArrangement, rowAlignment) {
                SingleLineFormField(Modifier.weight(1F), { viewModel.updateNewEmployee(emailAddress = it) }, "Email Address", emailAddressKeyboardOptions)
                SingleLineFormField(Modifier.weight(1F), { viewModel.updateNewEmployee(contactNumber = it) }, "Contact Number", phoneNumberKeyboardOptions)

            }
        }
    }
}


@Composable
fun EmployeeRolePicker(isExpanded: Boolean, onDismissRequest: () -> Unit, onSelect: (EmployeeRole) -> Unit) {
    DropdownMenu(expanded = isExpanded, onDismissRequest = onDismissRequest) {
        DropdownMenuItem(
            text = { Text(text = "Admin") },
            onClick = {
                onSelect(EmployeeRole.ADMIN)
            },
        )
        DropdownMenuItem(
            text = { Text(text = "Coordinator") },
            onClick = {
                onSelect(EmployeeRole.COORDINATOR)
            },
        )
        DropdownMenuItem(
            text = { Text(text = "Freelancer") },
            onClick = {
                onSelect(EmployeeRole.FREELANCER)
            },
        )
    }
}
