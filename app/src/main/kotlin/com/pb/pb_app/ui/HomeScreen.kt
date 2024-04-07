package com.pb.pb_app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pb.pb_app.ui.viewmodels.HomeViewModels
import com.pb.pb_app.ui.viewmodels.HomeViewModels.AdminViewModel
import com.pb.pb_app.ui.viewmodels.HomeViewModels.CoordinatorViewModel
import com.pb.pb_app.ui.viewmodels.HomeViewModels.FreelancerViewModel
import com.pb.pb_app.utils.Destination.LOGIN_SCREEN
import com.pb.pb_app.utils.models.Resource
import com.pb.pb_app.utils.models.Resource.Failure
import com.pb.pb_app.utils.models.Resource.Loading
import com.pb.pb_app.utils.models.Resource.Success
import com.pb.pb_app.utils.models.employees.Admin
import com.pb.pb_app.utils.models.employees.Coordinator
import com.pb.pb_app.utils.models.employees.Freelancer
import com.pb.pb_app.utils.models.employees.GenericEmployee

private const val TAG = "HomeScreen"

@Composable
fun HomeScreen(userResource: Resource<GenericEmployee>, navController: NavController) {
    when (userResource) {
        is Success -> {
            val viewModel: AdminViewModel = viewModel(factory = HomeViewModels.getHomeViewModelInstance(userResource.data))
            val user = viewModel.user
            HomeScreenLayout(user, viewModel)
        }

        is Loading -> {
            throw IllegalStateException("How is blud still loading")
        }

        is Failure -> {
            navController.popBackStack()
            navController.navigate(LOGIN_SCREEN.route)
        }
    }
}

@Composable
private fun HomeScreenLayout(user: GenericEmployee, viewModel: HomeViewModels.HomeViewModel) {

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()
    ) {
        HomeHeader(user.name) {
            val borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)

            when (user) {
                is Admin -> TextButton(onClick = {  }, border = borderStroke) {
                    Icon(Icons.Default.PersonAdd, "Add employee button")
                    Text(text = "Create Employee")
                }

                else -> PBBooleanSwitch(initialState = user.isOnline) {
                    if (user is Freelancer) (viewModel as FreelancerViewModel).setOnlineStatus(it)
                    else if (user is Coordinator) (viewModel as CoordinatorViewModel).setOnlineStatus(it)
                }
            }
        }
        when (user) {
            is Admin -> {
                AdminLayout()
            }

            is Coordinator -> {
                CoordinatorLayout()
            }

            is Freelancer -> {
                FreelancerLayout()
            }
        }
    }
}

@Composable
private fun FreelancerLayout() {
    val viewModel: FreelancerViewModel = viewModel()

    val newInquiries by (viewModel).newInquiries.collectAsState()

    if (newInquiries !is Success) return

    SectionHeader("Enquiries")
    HorizontalCardCarousel {
        items((newInquiries as Success).data) { inquiry ->
            InquiryCard(enquiry = inquiry) {
                BooleanButton("Accept", "Reject") { response ->

                }
            }
        }
    }
    Table("Inquiries") {
        itemsIndexed((newInquiries as Success).data) { index, inquiry ->
            TableRow(Modifier, index % 2 == 0, { Text(text = inquiry.name) }, { Text(text = inquiry.description) }, { Text(text = inquiry.status.name) })
        }
    }
}

@Composable
private fun CoordinatorLayout() {
    val viewModel: CoordinatorViewModel = viewModel()
    val freelancers by (viewModel).freelancers.collectAsState()
    val newInquiries by (viewModel).newEnquiries.collectAsState()
    val otherInquiries by (viewModel).otherEnquiries.collectAsState()


    if (freelancers !is Success) return
    if (newInquiries !is Success) return

    SectionHeader("Enquiries")
    HorizontalCardCarousel {
        items((newInquiries as Success).data) { inquiry ->
            InquiryCard(enquiry = inquiry) {


            }
        }
    }
    Table("Inquiries") {
        itemsIndexed((otherInquiries as Success).data) { index, inquiry ->
            TableRow(Modifier, index % 2 == 0, { Text(text = inquiry.name) }, { Text(text = inquiry.description) }, { Text(text = inquiry.status.name) })
        }
    }
    SectionHeader("Employee Status")
    Table(title = "Free") {
        itemsIndexed((freelancers as Success).data) { index, coordinator ->
            TableRow(Modifier, index % 2 == 0, { Text(text = coordinator.employeeId) }, { Text(text = coordinator.name) }, { Text(text = coordinator.isOnline.toString()) })
        }
    }
    return
}

@Composable
private fun AdminLayout() {
    val viewModel: AdminViewModel = viewModel()
    val coordinators by (viewModel).coordinators.collectAsState()
    val freelancers by (viewModel).freelancers.collectAsState()
    val inquiries by (viewModel).newEnquiries.collectAsState()

    if (coordinators !is Success) return
    if (freelancers !is Success) return
    if (inquiries !is Success) return

    SectionHeader("Enquiries")
    HorizontalCardCarousel {
        items((inquiries as Success).data) { inquiry ->
            InquiryCard(enquiry = inquiry) {
                Button(onClick = { }) {
                    Text(text = "Accept")
                }
            }
        }
    }
    Table("Inquiries") {
        itemsIndexed((inquiries as Success).data) { index, inquiry ->
            TableRow(Modifier, index % 2 == 0, { Text(text = inquiry.name) }, { Text(text = inquiry.description) }, { Text(text = inquiry.status.name) })
        }
    }
    SectionHeader("Employee Status")
    Table(title = "Coordinators") {
        itemsIndexed((coordinators as Success).data) { index, coordinator ->
            TableRow(Modifier, index % 2 == 0, { Text(text = coordinator.employeeId) }, { Text(text = coordinator.name) }, { Text(text = coordinator.isOnline.toString()) })
        }
    }
    Table(title = "Freelancers") {
        itemsIndexed((freelancers as Success).data) { index, freelancer ->
            TableRow(Modifier, index % 2 == 0, { Text(text = freelancer.employeeId) }, { Text(text = freelancer.name) }, { Text(text = freelancer.isOnline.toString()) })
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
        GreetingText(name = employeeName)
        headerExtras()
    }
}

@Composable
private fun GreetingText(name: String) {
    Text(
        "Greetings, ${name}!",
        style = MaterialTheme.typography.titleLarge,
        fontFamily = FontFamily.Serif,
        color = MaterialTheme.colorScheme.primary,
        fontStyle = FontStyle.Italic
    )
}