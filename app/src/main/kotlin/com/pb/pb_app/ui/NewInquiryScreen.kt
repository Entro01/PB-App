package com.pb.pb_app.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pb.pb_app.data.Constants
import com.pb.pb_app.ui.reusables.BigFormField
import com.pb.pb_app.ui.reusables.DatePicker
import com.pb.pb_app.ui.reusables.PBMediumTopBar
import com.pb.pb_app.ui.reusables.SingleLineFormField
import com.pb.pb_app.ui.reusables.phoneNumberKeyboardOptions
import com.pb.pb_app.viewmodels.NewInquiryViewModel
import java.time.LocalDate
import java.time.ZoneId

private const val TAG = "NewInquiryScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NewInquiryScreen(navController: NavController = rememberNavController()) {
    val tomorrow = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant().toEpochMilli()
    val viewModel: NewInquiryViewModel = viewModel(factory = NewInquiryViewModel.factory)
    val newInquiry by viewModel.newInquiry.collectAsState()
    val isSaveButtonEnabled by viewModel.shouldEnableSaveButton.collectAsState()
    val futureDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return tomorrow <= utcTimeMillis
        }
    }

    Log.e(TAG, "NewInquiryScreen: $tomorrow")


    Scaffold(
        Modifier.fillMaxSize(),
        topBar = { PBMediumTopBar("New Inquiry", isSaveButtonEnabled) { viewModel.createNewInquiry(); navController.navigateUp() } }) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(bottom = 6.dp, start = 6.dp, end = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val rowModifier = Modifier.padding(8.dp, 4.dp)
            val arrangement = Arrangement.spacedBy(8.dp)
            val leadingIconModifier = Modifier
                .padding(8.dp)
                .size(32.dp)

            val datePickerState = rememberDatePickerState(selectableDates = futureDates)

            var shouldShowDeadlinePickerDialog by remember { mutableStateOf(false) }

            if (shouldShowDeadlinePickerDialog) {
                DatePicker(datePickerState, onDismissRequest = { shouldShowDeadlinePickerDialog = false }) {
                    viewModel.updateNewInquiry(deadlineMillis = it)
                    shouldShowDeadlinePickerDialog = false
                }
            }

            Row(rowModifier, arrangement) {
                Icon(Icons.Default.Title, modifier = leadingIconModifier, contentDescription = "new inquiry name icon")
                SingleLineFormField(
                    modifier = Modifier.weight(1F),
                    onTextChange = { viewModel.updateNewInquiry(name = it) },
                    placeholder = "Name"
                )
                IconButton(onClick = { shouldShowDeadlinePickerDialog = true }) {
                    val tint = if (newInquiry.deadlineMillis <= 0) MaterialTheme.colorScheme.error else LocalContentColor.current

                    Icon(Icons.Default.CalendarMonth, "date picker button icon", tint = tint)
                }
            }

            Row(rowModifier, arrangement) {
                Icon(Icons.Default.Phone, modifier = leadingIconModifier, contentDescription = "new inquiry phone icon")

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
                Icon(Icons.Default.Description, modifier = leadingIconModifier, contentDescription = "new inquiry description icon")
                BigFormField(Modifier.fillMaxHeight(), onTextChange = { viewModel.updateNewInquiry(description = it) }, "Description")
            }

            Row(rowModifier.weight(1F), arrangement) {
                Icon(Icons.Default.House, modifier = leadingIconModifier, contentDescription = "new inquiry address icon")
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