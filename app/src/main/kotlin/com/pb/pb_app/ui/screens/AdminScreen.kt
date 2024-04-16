package com.pb.pb_app.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pb.pb_app.data.enums.EmployeeRole
import com.pb.pb_app.data.models.Resource
import com.pb.pb_app.ui.CommonLayout
import com.pb.pb_app.ui.enums.Destination
import com.pb.pb_app.viewmodels.AdminViewModel
import com.pb.pb_app.viewmodels.HomeViewModel

private const val TAG = "AdminScreen"

@Composable
fun AdminScreen(navController: NavController) {
    val viewModel: AdminViewModel = viewModel(factory = HomeViewModel.factory(EmployeeRole.ADMIN))
    val self by viewModel.self.collectAsState()
    val coordinators by viewModel.coordinators.collectAsState()
    val freelancers by viewModel.freelancers.collectAsState()
    val urgentInquiries by viewModel.urgentInquiries.collectAsState()
    val miscInquiries by viewModel.miscInquiries.collectAsState()

    if (self !is Resource.Success) {
        Log.e(TAG, "AdminScreen: 1")
        return
    }
    if (coordinators !is Resource.Success) {
        Log.e(TAG, "AdminScreen: ${(coordinators as Resource.Failure).message}")

        return
    }
    if (freelancers !is Resource.Success) {
        Log.e(TAG, "AdminScreen: 3")

        return
    }
    if (urgentInquiries !is Resource.Success) {
        Log.e(TAG, "AdminScreen: 4")

        return
    }
    if (miscInquiries !is Resource.Success) {
        Log.e(TAG, "AdminScreen: 1")

        return
    }

    CommonLayout(
        name = self.data!!.name,
        role = EmployeeRole.ADMIN,
        freelancers = freelancers.data,
        coordinators = coordinators.data,
        miscInquiries = miscInquiries.data!!,
        urgentInquiries = urgentInquiries.data!!,
        onNewEmployeeClicked = { navController.navigate(Destination.NEW_EMPLOYEE_SCREEN.route) },
        onCoordinatorSelected = { inquiryId, coordinatorId -> viewModel.assignCoordinator(coordinatorId, inquiryId) },
        onInquiryDeleted = { inquiryId -> viewModel.deleteInquiry(inquiryId) },
        onTagsAdded = { inquiryId, tags -> viewModel.markResolved(inquiryId, tags) }
    )

}