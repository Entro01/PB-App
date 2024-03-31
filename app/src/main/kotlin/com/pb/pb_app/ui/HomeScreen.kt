package com.pb.pb_app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.pb.pb_app.MainViewModel
import com.pb.pb_app.utils.NavDestinations.LOGIN_SCREEN
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.employees.Admin
import com.pb.pb_app.utils.models.employees.Coordinator
import com.pb.pb_app.utils.models.employees.Freelancer
import com.pb.pb_app.utils.models.employees.GenericEmployee
import com.pb.pb_app.utils.models.projects.Enquiry

private const val TAG = "HomeScreen"

class HomeScreenComposables(private val viewModel: MainViewModel, private val navController: NavController) {
    @Composable
    fun HomeScreen() {
        val userResource = viewModel.userResource.collectAsState()

        when (userResource.value) {
            is Resource.Success -> {
                HomeScreenLayout()
            }

            is Resource.Loading -> {
                throw IllegalStateException()
            }

            is Resource.Failure -> {
                navController.popBackStack()
                navController.navigate(LOGIN_SCREEN)
            }
        }

    }


    @Composable
    private fun HomeScreenLayout() {
        val user = viewModel.userResource.collectAsState().value.data ?: return

        Box {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()
            ) {
                HomeScreenHeader(user.name)
                when (user) {
                    is Admin -> AdminContentLayout(Modifier.fillMaxWidth())
                    is Coordinator -> CoordinatorContentLayout(Modifier.fillMaxWidth())
                    is Freelancer -> FreelancerLayout(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }

    @Composable
    private fun AdminContentLayout(modifier: Modifier = Modifier) {
        val projectCoordinators = viewModel.employees.collectAsState().value.filter { it.username.startsWith("PB-PC") }
        val freelancers = viewModel.employees.collectAsState().value.filter { it.username.startsWith("PB-FR") }

        Column(modifier) {
            SectionHeader("Enquiries")
            EnquiryCarousel()
            EmployeeList(title = "Freelancers", genericEmployees = freelancers)
            EmployeeList(title = "Project Coordinators", genericEmployees = projectCoordinators)
        }
    }

    @Composable
    private fun CoordinatorContentLayout(modifier: Modifier) {
        Column(modifier) {
            SectionHeader("Enquiries")
            EnquiryCarousel()
            EmployeeList(title = "Freelancers Online", viewModel.employees.collectAsState().value)
        }
    }

    @Composable
    private fun FreelancerLayout(modifier: Modifier) {
        Column(modifier) {
            SectionHeader("Enquiries")
            EnquiryCarousel()
        }

    }


    @Composable
    fun PBDialog(onDismissRequest: () -> Unit, label: String, onOkClicked: (String) -> Unit) {
        var text by remember {
            mutableStateOf("")
        }

        Dialog(onDismissRequest = onDismissRequest) {
            Surface(color = MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.extraLarge) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = label, style = MaterialTheme.typography.labelLarge)
                    TextField(value = text, onValueChange = { text = it }, leadingIcon = {
                        Text("OK", style = MaterialTheme.typography.labelMedium, modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                if (text.isNotEmpty() && text.isNotBlank()) {
                                    onOkClicked(text)
                                }
                            })
                    })
                }
            }
        }
    }


    @Composable
    fun EnquiryCard(modifier: Modifier = Modifier, enquiry: Enquiry) {
        val user = viewModel.userResource.value.data ?: return
        var shouldShowDialog by remember {
            mutableStateOf<Boolean?>(null)
        }

        if (shouldShowDialog == true) {
            PBDialog(onDismissRequest = { shouldShowDialog = false }, label = "Select Employee") {
                when (user) {
                    is Admin -> viewModel.acceptEnquiry(enquiry, it)
                    is Coordinator -> viewModel.acceptAndAssignPC(enquiry, it)
                    is Freelancer -> viewModel.acceptEnquiryAsFR(enquiry)
                }
            }
        }

        Column(
            modifier
                .aspectRatio(1.0F)
                .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EnquiryTitleText(title = enquiry.name)
            EnquiryDescriptionText(Modifier.weight(1F), projectDescription = enquiry.description)
            BooleanButton {
                shouldShowDialog = it
            }
        }
    }


    @Composable
    fun EmployeeList(title: String, genericEmployees: List<GenericEmployee>) {
        val borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)

        Column(
            Modifier
                .border(borderStroke)
                .padding(4.dp)
        ) {
            Text(title, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .heightIn(120.dp, 180.dp)
            ) {
                items(genericEmployees) {
                    EmployeeCell(employee = it)
                }
            }
        }
    }

    @Composable
    fun BooleanButton(onInteracted: (Boolean) -> Unit) {
        Row {
            TextButton(onClick = {
                onInteracted(true)
            }) {
                Text(text = "Accept")
            }
            TextButton(onClick = {
                onInteracted(false)
            }) {
                Text(text = "Reject")
            }
        }
    }

    @Composable
    fun EnquiryCarousel(
        modifier: Modifier = Modifier
    ) {
        val enquiries = viewModel.enquiries.collectAsState().value
        LazyRow(
            modifier
                .height(250.dp)
                .fillMaxWidth(), contentPadding = PaddingValues(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(enquiries) {
                EnquiryCard(enquiry = it)
            }
        }
    }


    @Composable
    private fun HomeScreenHeader(
        employeeName: String
    ) {
        val user = viewModel.userResource.collectAsState().value.data ?: return

        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            PBHomeGreetingText(name = employeeName)
            if (user !is Admin) {
                PBBooleanSwitch(initialState = user.isUserOnline) {
                    viewModel.setOnlineStatus(it)
                }
            } else {
                TextButton(onClick = {

                }, border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)) {
                    Text("New Employee")
                }
            }
        }
    }

    @Composable
    private fun PBHomeGreetingText(name: String) {
        Text(
            "Greetings, ${name}!",
            style = MaterialTheme.typography.titleLarge,
            fontFamily = FontFamily.Serif,
            color = MaterialTheme.colorScheme.primary,
            fontStyle = FontStyle.Italic
        )
    }

}