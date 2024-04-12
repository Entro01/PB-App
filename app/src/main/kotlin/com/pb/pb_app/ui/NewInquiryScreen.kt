package com.pb.pb_app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.pb.pb_app.ui.viewmodels.NewInquiryViewModel
import com.pb.pb_app.utils.Constants
import com.pb.pb_app.utils.reusables.BigFormField
import com.pb.pb_app.utils.reusables.PBMediumTopBar
import com.pb.pb_app.utils.reusables.SingleLineFormField
import com.pb.pb_app.utils.reusables.phoneNumberKeyboardOptions

private const val TAG = "NewEnquiryScreen"

/*   SERVICES = [
        ('PROJECT', 'Projects Preparation'),
        ('MODELS', 'Models Preparation'),
        ('ACADEMIC_WRITING', 'Academic Writing (Thesis, Dissertation, SOP, etc.)'),
        ('MS_OFFICE', 'MS Office (PPT, Word, Excel)'),
        ('DIY', 'DIY Crafts'),
        ('PAINTING', 'Posters/Painting'),
        ('GRAPHIC', 'Graphic Design'),
        ('PROGRAMMING', 'Programming (Java, Phython, etc.)'),
        ('GRAFFITI', 'Wall Painting / Graffiti'),
        ('HOMEWORK', 'Holidays Homework'),
        ('OTHERS', 'Others')
    ]*/


@Preview
@Composable
fun NewInquiryScreen(navController: NavController = rememberNavController()) {
    val viewModel: NewInquiryViewModel = viewModel(factory = NewInquiryViewModel.factory)
    var shouldShowDeadlinePickerDialog by remember { mutableStateOf(false) }

    if (shouldShowDeadlinePickerDialog) {
        InquiryDeadlinePickerDialog(onDismissRequest = { shouldShowDeadlinePickerDialog = false }) {
            viewModel.updateNewInquiry(deadlineMillis = it)
            shouldShowDeadlinePickerDialog = false
        }
    }

    val isSaveButtonEnabled by viewModel.shouldEnableSaveButton.collectAsState()

    Scaffold(Modifier.fillMaxSize(), topBar = { PBMediumTopBar("New Inquiry", isSaveButtonEnabled) { viewModel.createNewInquiry(); navController.navigateUp() } }) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(bottom = 6.dp, start = 6.dp, end = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val rowArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            val rowAlignment = Alignment.CenterVertically
            val rowModifier = Modifier.fillMaxWidth()

            Row(rowModifier, rowArrangement, rowAlignment) {
                SingleLineFormField(Modifier.weight(1F), onTextChange = { viewModel.updateNewInquiry(name = it) }, "Name")

            }
            Row(rowModifier, rowArrangement, rowAlignment) {
                var isServicesExpanded by remember {
                    mutableStateOf(false)
                }

                SingleLineFormField(Modifier.weight(1F), onTextChange = { viewModel.updateNewInquiry(contactNumber = it) }, "Contact Number", phoneNumberKeyboardOptions)

                IconButton(onClick = { shouldShowDeadlinePickerDialog = true }) {
                    Icon(Icons.Default.AlarmAdd, contentDescription = "deadline picker")
                }
                TextButton(onClick = { isServicesExpanded = true }) {
                    var serviceName by remember {
                        mutableStateOf("")
                    }

                    Text(text = serviceName.ifBlank { "Pick service" })

                    ServicesDropDown(isExpanded = isServicesExpanded, onDismissRequest = { isServicesExpanded = false }) {
                        viewModel.updateNewInquiry(service = it); isServicesExpanded = false; serviceName = it
                    }
                }
            }

            BigFormField(onTextChange = { viewModel.updateNewInquiry(deliveryArea = it) }, placeholder = "Delivery Area")

            BigFormField(onTextChange = { viewModel.updateNewInquiry(description = it) }, "Description")
        }
    }
}

@Composable
fun ServicesDropDown(isExpanded: Boolean, onDismissRequest: () -> Unit, onServiceSelected: (String) -> Unit) {
    DropdownMenu(expanded = isExpanded, onDismissRequest = onDismissRequest) {
        for (each in Constants.servicesList) {
            DropdownMenuItem(text = { Text(each) }, onClick = { onServiceSelected(each) })
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InquiryDeadlinePickerDialog(onDismissRequest: () -> Unit, onConfirm: (Long) -> Unit) {
    val datePickerState = rememberDatePickerState(System.currentTimeMillis())

    DatePickerDialog(onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(onClick = { onConfirm(datePickerState.selectedDateMillis!!) }) {
            Text(text = "OK")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}