package com.pb.pb_app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pb.pb_app.data.enums.EmployeeRole
import com.pb.pb_app.data.models.Resource
import com.pb.pb_app.ui.CommonLayout
import com.pb.pb_app.viewmodels.CoordinatorViewModel
import com.pb.pb_app.viewmodels.HomeViewModel

@Composable
fun CoordinatorScreen(navController: NavController) {
    val viewModel: CoordinatorViewModel = viewModel(factory = HomeViewModel.factory(EmployeeRole.COORDINATOR))
    val self by viewModel.self.collectAsState()
    val freelancers by (viewModel).freelancers.collectAsState()
    val urgentInquiries by (viewModel).urgentInquiries.collectAsState()
    val miscInquiries by (viewModel).miscInquiries.collectAsState()


    if (self !is Resource.Success) return
    if (freelancers !is Resource.Success) return
    if (miscInquiries !is Resource.Success) return
    if (urgentInquiries !is Resource.Success) return

    CommonLayout(
        name = self.data!!.name,
        role = EmployeeRole.COORDINATOR,
        freelancers = freelancers.data!!,
        coordinators = null,
        onOnlineStatusToggled = { viewModel.setOnlineStatus(it) },
        miscInquiries = miscInquiries.data!!,
        urgentInquiries = urgentInquiries.data!!,
        onFreelancerChecked = { freelancerId -> viewModel.appendFreelancer(freelancerId) },
        onFreelancerRequested = { viewModel.acceptUrgentInquiry(it); },
        onFreelancerAssigned = {  freelancerId, inquiryId -> viewModel.assignFreelancer(freelancerId, inquiryId)},
        onInquiryRejected = { viewModel.rejectUrgentInquiry(it) }
    )
}
