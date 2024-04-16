package com.pb.pb_app.ui

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pb.pb_app.data.enums.EmployeeRole
import com.pb.pb_app.data.models.abstracts.BaseEmployee
import com.pb.pb_app.data.models.employees.Coordinator
import com.pb.pb_app.data.models.employees.Freelancer
import com.pb.pb_app.data.models.inquiries.Inquiry
import com.pb.pb_app.data.models.inquiries.InquiryStatus
import com.pb.pb_app.ui.reusables.HorizontalCarousel
import com.pb.pb_app.ui.reusables.PBBooleanSwitch
import com.pb.pb_app.ui.reusables.SingleLineFormField

private const val TAG = "HomeScreens"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonLayout(
    name: String,
    role: EmployeeRole,
    freelancers: List<Freelancer>?,
    coordinators: List<Coordinator>?,
    onOnlineStatusToggled: ((Boolean) -> Unit) = {},
    miscInquiries: List<Inquiry>,
    urgentInquiries: List<Inquiry>,
    onNewEmployeeClicked: () -> Unit = {},
    onCoordinatorSelected: ((Int, String) -> Unit) = { _, _ -> },
    onInquiryDeleted: (Int) -> Unit = {},
    onFreelancerChecked: ((String) -> Unit) = { },
    onFreelancerAssigned: ((String, Int) -> Unit) = { _, _ -> },
    onInquiryAcceptedByFreelancer: ((Int) -> Unit) = {},
    onInquiryRejected: ((Int) -> Unit) = {},
    onFreelancerRequested: (Int) -> Unit = {},
    onTagsAdded: (Int, String) -> Unit = { _, _ -> }
) {

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(bottom = if (role == EmployeeRole.ADMIN) 100.dp else 8.dp, start = 8.dp, end = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
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
            val inquiry = urgentInquiries[inquiryIndex]
            when (inquiry.status) {
                is InquiryStatus.Unassigned -> {
                    val sheetState = rememberModalBottomSheetState()

                    var pickingCoordinators by remember {
                        mutableStateOf(false)
                    }

                    if (pickingCoordinators) {
                        EmployeeSingularPickerDialog(
                            "Select Freelancer",
                            sheetState,
                            employees = coordinators!!,
                            onDismissRequest = { pickingCoordinators = false },
                            {
                                onCoordinatorSelected(inquiry.id, it)
                                pickingCoordinators = false
                            },
                        )
                        LaunchedEffect(Unit) { sheetState.show() }
                    } else LaunchedEffect(Unit) {
                        sheetState.hide()
                    }

                    UnassignedInquiryCard(inquiry = inquiry, onAccept = { pickingCoordinators = true }, onReject = { onInquiryDeleted(inquiry.id) })
                }

                is InquiryStatus.CoordinatorRequested -> {
                    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

                    var pickingFreelancers by remember {
                        mutableStateOf(false)
                    }

                    if (pickingFreelancers) {
                        EmployeeMultiPickerDialog(
                            sheetState,
                            employees = freelancers!!,
                            onDismissRequest = { pickingFreelancers = false },
                            onEmployeeSelect = { onFreelancerChecked(it) }
                        ) {
                            onFreelancerRequested(inquiry.id)
                            pickingFreelancers = false
                        }
                        LaunchedEffect(Unit) {
                            sheetState.show()
                        }
                    } else LaunchedEffect(Unit) { sheetState.hide() }

                    CoordinatorRequestedInquiryCard(inquiry = inquiry, onAccept = { pickingFreelancers = true }, onReject = { onInquiryRejected(inquiry.id) })
                }

                is InquiryStatus.FreelancerRequested -> {
                    if (role == EmployeeRole.FREELANCER) {
                        FreelancerRequestedInquiryCardFR(inquiry = inquiry, onAccept = { onInquiryAcceptedByFreelancer(inquiry.id) }) {
                            onInquiryRejected(inquiry.id)
                        }
                    } else if (role == EmployeeRole.COORDINATOR) {
                        val sheetState = rememberModalBottomSheetState()

                        var pickingFreelancer by remember {
                            mutableStateOf(false)
                        }

                        if (pickingFreelancer) {
                            EmployeeSingularPickerDialog(
                                "Select Freelancer",
                                sheetState, employees = freelancers!!, onDismissRequest = { pickingFreelancer = false },
                                {
                                    onFreelancerAssigned(it, inquiry.id)
                                    pickingFreelancer = false
                                },
                            )
                            LaunchedEffect(Unit) { sheetState.show() }
                        } else LaunchedEffect(Unit) { sheetState.hide() }



                        FreelancerRequestedInquiryCardPC(inquiry = inquiry, onAssign = { pickingFreelancer = true })
                    }
                }

                is InquiryStatus.FreelancerAssigned -> {
                    var addingTags by remember {
                        mutableStateOf(false)
                    }

                    if (addingTags) {
                        TagsDialog({ addingTags = false }) { tags -> onTagsAdded(inquiry.id, tags); addingTags = false }
                    }

                    FreelancerAssignedInquiryCard(inquiry = inquiry) {
                        addingTags = true
                    }
                }

                else -> return@HorizontalCarousel
            }
        }

        Table("Other Inquiries", miscInquiries.size) {
            TableRow(Modifier,
                it % 2 == 0,
                { Text(text = miscInquiries[it].name) },
                { Text(text = miscInquiries[it].description) },
                { Text(text = miscInquiries[it].status.label) })
        }

        // Components restricted for Freelancers
        if (role != EmployeeRole.FREELANCER) {


            Table("Freelancers", freelancers!!.size) {
                TableRow(Modifier,
                    it % 2 == 0,
                    { Text(text = freelancers[it].employeeId) },
                    { Text(text = freelancers[it].name) },
                    { Text(text = if (freelancers[it].availabilityStatus) "Available" else "Unavailable") })
            }
            if (role == EmployeeRole.ADMIN) {
                Table("Coordinators", coordinators!!.size) {
                    TableRow(Modifier,
                        it % 2 == 0,
                        { Text(text = coordinators[it].employeeId) },
                        { Text(text = coordinators[it].name) },
                        { Text(text = if (coordinators[it].availabilityStatus) "Available" else "Unavailable") })

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsDialog(onDismissRequest: () -> Unit, onDoneClicked: (String) -> Unit) {
    var tags by remember {
        mutableStateOf("")
    }

    AlertDialog(onDismissRequest = { onDismissRequest() }, confirmButton = {
        TextButton(onClick = { onDoneClicked(tags) }) {
            Text("Done")
        }
    }, dismissButton = {
        TextButton(onClick = { onDismissRequest() }) {
            Text(text = "Cancel")
        }
    }, text = { SingleLineFormField(onTextChange = { tags = it }, placeholder = "Space separated tags") }, title = { Text("Enter Tags") })

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
fun UnassignedInquiryCard(modifier: Modifier = Modifier, inquiry: Inquiry, onAccept: () -> Unit, onReject: () -> Unit) {
    if (inquiry.status !is InquiryStatus.Unassigned) return
    val progress = { (System.currentTimeMillis() - inquiry.creationTime).toFloat() / (inquiry.deadlineMillis - inquiry.creationTime).toFloat() }

    Surface(
        shape = MaterialTheme.shapes.large,
        shadowElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .aspectRatio(1.0F)
            .fillMaxWidth(),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(inquiry.name, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = modifier.fillMaxWidth())
            Text(
                inquiry.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
            )
            LinearProgressIndicator(progress, modifier.fillMaxWidth(), MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surface, StrokeCap.Butt)
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)) {
                TextButton(onClick = onAccept) {
                    Text(text = "Request Coordinator")
                }

                TextButton(onClick = onReject) {
                    Text(text = "Reject")
                }
            }
        }
    }
}

@Composable
fun FreelancerAssignedInquiryCard(modifier: Modifier = Modifier, inquiry: Inquiry, onAddTagsClicked: () -> Unit) {
    if (inquiry.status !is InquiryStatus.FreelancerAssigned) return

    Surface(
        shape = MaterialTheme.shapes.large, shadowElevation = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier
            .aspectRatio(1.0F)
            .fillMaxWidth()

    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(inquiry.name, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = modifier.fillMaxWidth())
            Text(
                inquiry.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
            )
            Button(onClick = onAddTagsClicked, Modifier.fillMaxWidth()) {
                Text(text = "Add Tags")
            }
        }
    }
}

@Composable
fun CoordinatorRequestedInquiryCard(modifier: Modifier = Modifier, inquiry: Inquiry, onAccept: () -> Unit, onReject: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.large, shadowElevation = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier
            .aspectRatio(1.0F)
            .fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(inquiry.name, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = modifier.fillMaxWidth())
            Text(
                inquiry.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
            )
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)) {
                TextButton(onClick = onAccept) {
                    Text(text = "Request FR")
                }

                TextButton(onClick = onReject) {
                    Text(text = "Reject")
                }
            }
        }
    }
}

@Composable
fun FreelancerRequestedInquiryCardPC(modifier: Modifier = Modifier, inquiry: Inquiry, onAssign: () -> Unit) {
    val progress = { (System.currentTimeMillis() - inquiry.creationTime).toFloat() / (inquiry.creationTime - inquiry.deadlineMillis).toFloat() }

    val inquiryStatus = inquiry.status as InquiryStatus.FreelancerRequested
    val firstString = "${inquiryStatus.freelancerFirst} has " + if (inquiryStatus.firstResponse == true) "Accepted" else "Rejected"
    val secondString = "${inquiryStatus.freelancerSecond} has " + if (inquiryStatus.secondResponse == true) "Accepted" else "Rejected"
    val thirdString = "${inquiryStatus.freelancerThird} has " + if (inquiryStatus.thirdResponse == true) "Accepted" else "Rejected"


    Surface(
        shape = MaterialTheme.shapes.large, shadowElevation = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .aspectRatio(1.0F)
            .fillMaxWidth(),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
        ) {
            Text(inquiry.name, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = modifier.fillMaxWidth())
            Text(firstString, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth())
            Text(secondString, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth())
            Text(thirdString, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth())
            LinearProgressIndicator(progress, modifier.fillMaxWidth(), MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surface, StrokeCap.Butt)
            Button(onClick = onAssign) {
                Text(text = "Assign Freelancer")
            }
        }
    }
}

@Composable
fun FreelancerRequestedInquiryCardFR(modifier: Modifier = Modifier, inquiry: Inquiry, onAccept: () -> Unit, onReject: () -> Unit) {
    val inquiryStatus = (inquiry.status as InquiryStatus.FreelancerRequested)
    val progress = { (System.currentTimeMillis() - inquiryStatus.assignedTime).toFloat() / (inquiryStatus.assignedTime - inquiryStatus.firstCountDownMillis!!).toFloat() }
    Surface(
        shape = MaterialTheme.shapes.large,
        shadowElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .aspectRatio(1.0F)
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(inquiry.name, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = modifier.fillMaxWidth())
            Text(
                inquiry.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier
                    .weight(1F)
                    .fillMaxWidth()
            )
            LinearProgressIndicator(progress, modifier.fillMaxWidth(), MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surface, StrokeCap.Butt)
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)) {
                TextButton(onClick = onAccept) {
                    Text(text = "Accept")
                }

                TextButton(onClick = onReject) {
                    Text(text = "Reject")
                }
            }
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
fun EmployeeMultiPickerDialog(
    bottomSheetState: SheetState,
    employees: List<BaseEmployee>,
    onDismissRequest: () -> Unit,
    onEmployeeSelect: (employeeId: String) -> Unit,
    onDoneClicked: () -> Unit
) {

    ModalBottomSheet(sheetState = bottomSheetState, onDismissRequest = { onDismissRequest() }) {
        Column(
            Modifier
                .navigationBarsPadding()
                .padding(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "Select Freelancer", style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.CenterHorizontally)
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
                        val modifier = Modifier.weight(1F)
                        val centerAlign = TextAlign.Center

                        Text(text = employee.employeeId, modifier, textAlign = centerAlign)
                        Text(text = employee.name, modifier, textAlign = centerAlign)
                        Text(text = if (employee.availabilityStatus) "Available" else "Unavailable", modifier, textAlign = centerAlign)
                        Checkbox(checked = isChecked, onCheckedChange = { if (it) onEmployeeSelect(employee.employeeId); isChecked = !isChecked })
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceAround) {
                Button(onClick = onDoneClicked) {
                    Text(text = "Assign")
                }
                OutlinedButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeSingularPickerDialog(
    title: String, sheetState: SheetState, employees: List<BaseEmployee>, onDismissRequest: () -> Unit, onDoneClicked: (String) -> Unit,
) {
    ModalBottomSheet(sheetState = sheetState, onDismissRequest = onDismissRequest) {

        Column(
            Modifier
                .navigationBarsPadding()
                .padding(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            var selectedIndex by remember {
                mutableIntStateOf(-1)
            }

            LazyColumn {

                itemsIndexed(employees) { index, employee ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp), Arrangement.SpaceEvenly, Alignment.CenterVertically
                    ) {
                        val centerAlign = TextAlign.Center
                        val modifier = Modifier.weight(1F)

                        Text(text = employee.employeeId, modifier, textAlign = centerAlign)
                        Text(text = employee.name, modifier, textAlign = centerAlign)
                        Text(text = if (employee.availabilityStatus) "Available" else "Unavailable", modifier, textAlign = centerAlign)
                        RadioButton(selected = selectedIndex == index, onClick = { selectedIndex = index })
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), Arrangement.SpaceAround) {
                Button(onClick = { onDoneClicked(employees[selectedIndex].employeeId) }) {
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
            Modifier.fillMaxWidth()
        ) {

            items(itemCount) {
                cellLayout(it)
            }
        }
    }
}