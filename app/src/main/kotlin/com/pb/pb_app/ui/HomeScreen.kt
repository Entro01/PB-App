package com.pb.pb_app.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pb.pb_app.data.enums.EmployeeRole
import com.pb.pb_app.data.models.abstracts.BaseEmployee
import com.pb.pb_app.data.models.employees.Coordinator
import com.pb.pb_app.data.models.employees.Freelancer
import com.pb.pb_app.data.models.inquiries.Inquiry
import com.pb.pb_app.ui.reusables.HorizontalCarousel
import com.pb.pb_app.ui.reusables.PBBooleanSwitch

private const val TAG = "HomeScreens"


@Composable
fun CommonLayout(
    name: String,
    role: EmployeeRole,
    freelancers: List<Freelancer>?,
    coordinators: List<Coordinator>?,
    miscInquiries: List<Inquiry>?,
    urgentInquiries: List<Inquiry>,
    onNewEmployeeClicked: () -> Unit = {},
    onCoordinatorSelected: ((String) -> Unit) = {},
    onInquiryDeleted: (Int) -> Unit = {},
    onFreelancerSelected: ((String) -> Unit) = { },
    onInquiryAcceptedByFreelancer: ((Int) -> Unit) = {},
    onInquiryRejected: ((Int) -> Unit) = {},
    onOnlineStatusToggled: ((Boolean) -> Unit) = {},
    onDialogFinished: (Int) -> Unit = {}
) {

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp, start = 8.dp, end = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HomeHeader(name) {
            if (role == EmployeeRole.ADMIN) {
                val borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
                TextButton(onClick = { onNewEmployeeClicked() }, border = borderStroke) {
                    Icon(Icons.Default.PersonAdd, "Add employee button")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "New Employee")
                }
            } else {
                PBBooleanSwitch(initialState = false) {
                    onOnlineStatusToggled(it)
                }
            }
        }


        // Inquiries Carousel (shown at all times)
        Text(text = "Inquiries", modifier = Modifier.align(Alignment.CenterHorizontally), style = MaterialTheme.typography.titleMedium)
        HorizontalCarousel(Modifier, urgentInquiries.size) { inquiryIndex ->
            InquiryCard(inquiry = urgentInquiries[inquiryIndex]) {
                var inquiryInteractionBoolean: Boolean? by remember {
                    mutableStateOf(null)
                }
                if (role == EmployeeRole.ADMIN) {
                    if (inquiryInteractionBoolean == true) {
                        FreelancerPickerDialog(employees = coordinators!!, onDismissRequest = { inquiryInteractionBoolean = null }, { onCoordinatorSelected(it) }) {
                            onDialogFinished(urgentInquiries[inquiryIndex].id)
                            inquiryInteractionBoolean = null
                        }
                    } else if (inquiryInteractionBoolean == false) {
                        onInquiryDeleted(urgentInquiries[inquiryIndex].id)
                    }

                    TextButton(onClick = { inquiryInteractionBoolean = true }) {
                        Text(text = "Assign")
                    }
                    TextButton(onClick = { onInquiryDeleted(urgentInquiries[inquiryIndex].id) }) {
                        Text(text = "Delete")
                    }
                } else {
                    if (inquiryInteractionBoolean == true) {
                        if (role == EmployeeRole.COORDINATOR) FreelancerPickerDialog(employees = freelancers!!,
                            onDismissRequest = { inquiryInteractionBoolean = null },
                            onEmployeeSelect = { onFreelancerSelected(it) }) {
                            onDialogFinished(urgentInquiries[inquiryIndex].id)
                            inquiryInteractionBoolean = null
                        }
                        else if (role == EmployeeRole.FREELANCER) onInquiryAcceptedByFreelancer(urgentInquiries[inquiryIndex].id)

                    } else if (inquiryInteractionBoolean == false) {
                        onInquiryRejected(urgentInquiries[inquiryIndex].id)
                    }

                    TextButton(onClick = { inquiryInteractionBoolean = true }) {
                        Text(text = "Accept")
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    TextButton(onClick = { inquiryInteractionBoolean = false }) {
                        Text(text = "Reject")
                    }
                }
            }
        }

        // Components restricted for Freelancers
        if (role != EmployeeRole.FREELANCER) {
            Table("Other Inquiries", miscInquiries!!.size) {
                TableRow(Modifier,
                    it % 2 == 0,
                    { Text(text = miscInquiries[it].name) },
                    { Text(text = miscInquiries[it].description) },
                    { Text(text = miscInquiries[it].status.label) })
            }

            Table("Freelancers", freelancers!!.size) {
                TableRow(Modifier,
                    it % 2 == 0,
                    { Text(text = freelancers[it].employeeId) },
                    { Text(text = freelancers[it].name) },
                    { Text(text = freelancers[it].availabilityStatus.toString()) })
            }
            if (role == EmployeeRole.ADMIN) {
                Table("Coordinators", coordinators!!.size) {
                    TableRow(Modifier,
                        it % 2 == 0,
                        { Text(text = coordinators[it].employeeId) },
                        { Text(text = coordinators[it].name) },
                        { Text(text = coordinators[it].availabilityStatus.toString()) })

                }
            }
        }
    }
}

@Composable
private fun HomeHeader(
    employeeName: String, headerExtras: @Composable RowScope.() -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
    ) {
        HomeGreeting(name = employeeName)
        headerExtras()
    }
}

@Composable
private fun HomeGreeting(name: String) {
    Text(
        "Greetings, ${name}!",
        style = MaterialTheme.typography.titleLarge,
        fontFamily = FontFamily.Serif,
        fontStyle = FontStyle.Italic,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun InquiryCard(modifier: Modifier = Modifier, inquiry: Inquiry, actionButtons: @Composable RowScope.() -> Unit) {
    val progress = (System.currentTimeMillis() - inquiry.assigningMillis) / (inquiry.deadlineMillis - inquiry.assigningMillis).toFloat()

    Log.e(TAG, "InquiryCard: $progress")

    Column(
        modifier
            .aspectRatio(1.0F)
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.extraLarge)
            .padding(16.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(inquiry.name, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = modifier.fillMaxWidth())

        Text(
            modifier = modifier
                .padding(4.dp)
                .weight(1F), text = inquiry.description, style = MaterialTheme.typography.bodyMedium
        )

        LinearProgressIndicator(progress = { progress }, trackColor = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth())

        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            actionButtons()
        }
    }
}

@Composable
fun TableRow(modifier: Modifier = Modifier, isEven: Boolean, vararg cells: @Composable () -> Unit) {
    val bgColor = if (isEven) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    Row(
        modifier
            .background(bgColor)
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        for (cell in cells) {
            Box(Modifier.weight(1F), contentAlignment = Alignment.Center) {
                cell()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreelancerPickerDialog(
    employees: List<BaseEmployee>, onDismissRequest: () -> Unit, onEmployeeSelect: (employeeId: String) -> Unit, onDoneClicked: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {

        Column(
            Modifier
                .navigationBarsPadding()
                .padding(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "Select Freelancer",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )

            LazyColumn {
                items(employees) { employee ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp), Arrangement.SpaceEvenly, Alignment.CenterVertically
                    ) {
                        var isChecked by remember {
                            mutableStateOf(false)
                        }
                        Text(text = employee.employeeId)
                        Text(text = employee.name)
                        Text(text = if (employee.availabilityStatus) "Available" else "Unavailable")
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { if (it) onEmployeeSelect(employee.employeeId); isChecked = !isChecked })
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceAround) {
                OutlinedButton(onClick = onDoneClicked) {
                    Text(text = "Assign")
                }
                OutlinedButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            }
        }
    }
}


@Composable
fun Table(title: String, itemCount: Int, cellLayout: @Composable (Int) -> Unit) {
    val borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    Column(
        Modifier
            .border(borderStroke, MaterialTheme.shapes.large)
            .heightIn(150.dp, 200.dp)
    ) {
        Text(
            title, modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center
        )
        if (itemCount == 0) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Nothing to show",
                style = MaterialTheme.typography.labelMedium,
            )
        }

        LazyColumn(
            Modifier
                .fillMaxWidth()
        ) {

            items(itemCount) {
                cellLayout(it)
            }
        }
    }
}