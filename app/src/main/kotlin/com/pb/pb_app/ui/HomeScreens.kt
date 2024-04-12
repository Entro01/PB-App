package com.pb.pb_app.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pb.pb_app.ui.viewmodels.AdminViewModel
import com.pb.pb_app.ui.viewmodels.CoordinatorViewModel
import com.pb.pb_app.ui.viewmodels.FreelancerViewModel
import com.pb.pb_app.ui.viewmodels.HomeViewModel
import com.pb.pb_app.utils.models.Destination
import com.pb.pb_app.utils.models.Resource.Success
import com.pb.pb_app.utils.models.employees.BaseEmployee
import com.pb.pb_app.utils.models.employees.Coordinator
import com.pb.pb_app.utils.models.employees.EmployeeRole
import com.pb.pb_app.utils.models.employees.Freelancer
import com.pb.pb_app.utils.models.projects.Inquiry
import com.pb.pb_app.utils.reusables.HorizontalCarousel
import com.pb.pb_app.utils.reusables.PBBooleanSwitch

private const val TAG = "HomeScreens"

@Preview
@Composable
fun FreelancerScreen(navController: NavController = rememberNavController()) {
    val viewModel: FreelancerViewModel = viewModel(factory = HomeViewModel.factory(EmployeeRole.FREELANCER))
    val self by viewModel.self.collectAsState()
    val newInquiries by (viewModel).urgentInquiries.collectAsState()

    if (self !is Success<Freelancer>) return
    if (newInquiries !is Success<List<Inquiry>>) return

    CommonLayout(name = self.data!!.name,
        role = EmployeeRole.FREELANCER,
        freelancers = null,
        coordinators = null,
        miscInquiries = null,
        urgentInquiries = newInquiries.data!!,
        onNewEmployeeClicked = {},
        onOnlineStatusToggled = { viewModel.setOnlineStatus(it) },
        onInquiryAcceptedByFreelancer = { viewModel.acceptUrgentInquiry(it) })
}

@Composable
fun CoordinatorScreen(navController: NavController) {
    val viewModel: CoordinatorViewModel = viewModel(factory = HomeViewModel.factory(EmployeeRole.COORDINATOR))
    val self by viewModel.self.collectAsState()
    val freelancers by (viewModel).freelancers.collectAsState()
    val urgentInquiries by (viewModel).urgentInquiries.collectAsState()
    val miscInquiries by (viewModel).miscInquiries.collectAsState()


    if (self !is Success) return
    if (freelancers !is Success) return
    if (miscInquiries !is Success) return
    if (urgentInquiries !is Success) return

    CommonLayout(
        name = self.data!!.name,
        role = EmployeeRole.COORDINATOR,
        freelancers = freelancers.data!!,
        coordinators = null,
        miscInquiries = miscInquiries.data!!,
        urgentInquiries = urgentInquiries.data!!,
        onOnlineStatusToggled = { viewModel.setOnlineStatus(it) },
        onFreelancerSelected = { freelancerId -> viewModel.setSelectedEmployee(freelancerId) },
        onInquiryRejected = { viewModel.rejectUrgentInquiry(it) },
        onDialogFinished = { viewModel.acceptUrgentInquiry(it); viewModel.assignedFreelancer = "" }
    )
}

@Composable
fun AdminScreen(navController: NavController) {
    val viewModel: AdminViewModel = viewModel(factory = HomeViewModel.factory(EmployeeRole.ADMIN))
    val self by viewModel.self.collectAsState()
    val coordinators by viewModel.coordinators.collectAsState()
    val freelancers by viewModel.freelancers.collectAsState()
    val urgentInquiries by viewModel.urgentInquiries.collectAsState()
    val miscInquiries by viewModel.miscInquiries.collectAsState()

    if (self !is Success) return
    if (coordinators !is Success) return
    if (freelancers !is Success) return
    if (urgentInquiries !is Success) return
    if (miscInquiries !is Success) return

    CommonLayout(name = self.data!!.name,
        role = EmployeeRole.ADMIN,
        freelancers = freelancers.data,
        coordinators = coordinators.data,
        miscInquiries = miscInquiries.data,
        urgentInquiries = urgentInquiries.data!!,
        onNewEmployeeClicked = { navController.navigate(Destination.NEW_EMPLOYEE_SCREEN.route) },
        onCoordinatorSelected = { coordinatorId -> viewModel.appendCoordinator(coordinatorId) },
        onInquiryDeleted = { inquiryId -> viewModel.deleteInquiry(inquiryId) },
        onDialogFinished = { viewModel.assignCoordinator(it, System.currentTimeMillis()); viewModel.selectedCoordinators.clear() }
    )

}

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

    Column {
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

        Text(text = "Inquiries", modifier = Modifier.align(Alignment.CenterHorizontally), style = MaterialTheme.typography.labelLarge)
        HorizontalCarousel(Modifier, urgentInquiries.size) { inquiryIndex ->
            InquiryCard(inquiry = urgentInquiries[inquiryIndex]) {
                var inquiryInteractionBoolean: Boolean? by remember {
                    mutableStateOf(null)
                }
                if (role == EmployeeRole.ADMIN) {
                    if (inquiryInteractionBoolean == true) {
                        EmployeePickerDialog(employees = coordinators!!,
                            onDismissRequest = { inquiryInteractionBoolean = null },
                            { onCoordinatorSelected(it) }) {
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
                        if (role == EmployeeRole.COORDINATOR) EmployeePickerDialog(employees = freelancers!!,
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
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .padding(12.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InquiryTitleText(title = inquiry.name)
        InquiryDescriptionText(Modifier.weight(1F), projectDescription = inquiry.description)
        LinearProgressIndicator(progress = { progress }, trackColor = MaterialTheme.colorScheme.surface)
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            actionButtons()
        }
    }
}

@Composable
fun InquiryTitleText(modifier: Modifier = Modifier, title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = modifier.fillMaxWidth())
}

@Composable
fun InquiryDescriptionText(modifier: Modifier = Modifier, projectDescription: String) {
    Text(modifier = modifier.padding(4.dp), text = projectDescription, style = MaterialTheme.typography.bodySmall)
}


@Composable
fun TableRow(modifier: Modifier = Modifier, isEven: Boolean, vararg rowCells: @Composable RowScope.() -> Unit) {
    val bgColor = if (isEven) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    Row(
        modifier
            .background(bgColor)
            .padding(4.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (cell in rowCells) {
            cell()
        }
    }
}

@Composable
fun EmployeePickerDialog(
    employees: List<BaseEmployee>, onDismissRequest: () -> Unit, onEmployeeSelect: (employeeId: String) -> Unit, onDoneClicked: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {

        Surface(Modifier.sizeIn(250.dp, 120.dp), shape = MaterialTheme.shapes.large) {
            Column {
                LazyRow(contentPadding = PaddingValues(16.dp)) {
                    items(employees.size) {
                        var isSelected by remember {
                            mutableStateOf(false)
                        }
                        FilterChip(modifier = Modifier.padding(4.dp), selected = isSelected,
                            onClick = { isSelected = !isSelected; onEmployeeSelect(employees[it].employeeId) },
                            label = { Text(employees[it].employeeId) })
                    }
                }

                TextButton(modifier = Modifier.align(Alignment.End).padding(4.dp), onClick = { onDoneClicked() }) {
                    Text(text = "Done")
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
            .padding(4.dp)
            .border(borderStroke, RoundedCornerShape(4.dp))
    ) {
        Text(title, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .heightIn(120.dp, 180.dp)
        ) {

            if (itemCount == 0) item {
                Text(text = "Nothing to show", style = MaterialTheme.typography.labelSmall, modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
            }

            items(itemCount) {
                cellLayout(it)
            }
        }
    }
}