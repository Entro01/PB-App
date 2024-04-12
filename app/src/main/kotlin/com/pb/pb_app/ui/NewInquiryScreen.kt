package com.pb.pb_app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.pb.pb_app.data.Constants
import com.pb.pb_app.ui.reusables.BigFormField
import com.pb.pb_app.ui.reusables.PBMediumTopBar
import com.pb.pb_app.ui.reusables.SingleLineFormField
import com.pb.pb_app.ui.reusables.phoneNumberKeyboardOptions
import com.pb.pb_app.viewmodels.NewInquiryViewModel

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

    val isSaveButtonEnabled by viewModel.shouldEnableSaveButton.collectAsState()

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = { PBMediumTopBar("New Inquiry", isSaveButtonEnabled) { viewModel.createNewInquiry(); navController.navigateUp() } }) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(bottom = 6.dp, start = 6.dp, end = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val rowModifier = Modifier.padding(8.dp, 4.dp)
            val arrangement = Arrangement.spacedBy(8.dp)
            val iconModifier = Modifier
                .padding(8.dp)
                .size(32.dp)
            var shouldShowDeadlinePickerDialog by remember { mutableStateOf(false) }

            if (shouldShowDeadlinePickerDialog) {
                InquiryDeadlinePickerDialog(onDismissRequest = { shouldShowDeadlinePickerDialog = false }) {
                    viewModel.updateNewInquiry(deadlineMillis = it)
                    shouldShowDeadlinePickerDialog = false
                }
            }


            Row(rowModifier, arrangement) {
                Icon(Icons.Default.Title, modifier = iconModifier, contentDescription = "new inquiry name icon")
                SingleLineFormField(
                    modifier = Modifier.weight(1F),
                    onTextChange = { viewModel.updateNewInquiry(name = it) },
                    placeholder = "Name"
                )
                IconButton(onClick = { shouldShowDeadlinePickerDialog = true }) {
                    Icon(Icons.Default.CalendarMonth, "")
                }
            }

            Row(rowModifier, arrangement) {
                Icon(Icons.Default.Phone, modifier = iconModifier, contentDescription = "new inquiry phone icon")

                SingleLineFormField(
                    modifier = Modifier.weight(1f),
                    onTextChange = { viewModel.updateNewInquiry(contactNumber = it) },
                    placeholder = "Contact Number",
                    keyboardOptions = phoneNumberKeyboardOptions
                )
            }

            ServicesChipList {
                viewModel.updateNewInquiry(service = it)
            }

            Row(rowModifier.weight(1F), arrangement) {
                Icon(Icons.Default.Description, modifier = iconModifier, contentDescription = "new inquiry description icon")
                BigFormField(Modifier.fillMaxHeight(), onTextChange = { viewModel.updateNewInquiry(description = it) }, "Description")
            }

            Row(rowModifier.weight(1F), arrangement) {
                Icon(Icons.Default.House, modifier = iconModifier, contentDescription = "new inquiry address icon")
                BigFormField(Modifier.fillMaxHeight(), onTextChange = { viewModel.updateNewInquiry(deliveryArea = it) }, placeholder = "Delivery Area")
            }
        }
    }
}

@Composable
fun ServicesChipList(onServiceSelected: (String) -> Unit) {
    var selectedIndex by remember {
        mutableIntStateOf(-1)
    }

    LazyRow(Modifier.padding(4.dp), contentPadding = PaddingValues(4.dp)) {
        itemsIndexed(Constants.servicesList) { index: Int, item: String ->
            FilterChip(
                modifier = Modifier.padding(horizontal = 2.dp),
                selected = selectedIndex == index,
                onClick = { selectedIndex = index; onServiceSelected(item) },
                label = { Text(text = item) },
            )
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