package com.pb.pb_app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pb.pb_app.data.enums.EmployeeRole
import com.pb.pb_app.data.models.Resource
import com.pb.pb_app.data.models.inquiries.Inquiry
import com.pb.pb_app.ui.CommonLayout
import com.pb.pb_app.viewmodels.FreelancerViewModel
import com.pb.pb_app.viewmodels.HomeViewModel

@Preview
@Composable
fun FreelancerScreen(navController: NavController = rememberNavController()) {
    val viewModel: FreelancerViewModel = viewModel(factory = HomeViewModel.factory(EmployeeRole.FREELANCER))
    val self by viewModel.self.collectAsState()
    val urgentInquiries by viewModel.urgentInquiries.collectAsState()
    val miscInquiries by viewModel.miscInquiries.collectAsState()

    if (self !is Resource.Success) return
    if (urgentInquiries !is Resource.Success) return
    if (miscInquiries !is Resource.Success) return

    CommonLayout(name = self.data!!.name,
        role = EmployeeRole.FREELANCER,
        freelancers = null,
        coordinators = null,
        miscInquiries = miscInquiries.data!!,
        urgentInquiries = urgentInquiries.data!!,
        onNewEmployeeClicked = {},
        onOnlineStatusToggled = { viewModel.setOnlineStatus(it) },
        onInquiryAcceptedByFreelancer = { viewModel.acceptUrgentInquiry(it) },
        onInquiryRejected = { viewModel.rejectUrgentInquiry(it) }
    )
}