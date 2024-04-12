package com.pb.pb_app.utils

object Constants {
    object InquiryStatusLabels {
        const val UNASSIGNED = "Unassigned"
        const val COORDINATOR_REQUESTED = "CoordinatorRequested"
        const val FREELANCER_REQUESTED = "FreelancerRequested"
        const val FREELANCER_ACCEPTED = "FreelancerAccepted"
        const val FREELANCER_ASSIGNED = "FreelancerAssigned"
        const val INQUIRY_RESOLVED = "InquiryResolved"
    }

    object Services {
        const val PROJECT = "Projects Preparation"
        const val MODELS = "Models Preparation"
        const val ACADEMIC_WRITING = "Academic Writing"
        const val MS_OFFICE = "MS Office"
        const val DIY = "DIY Crafts"
        const val PAINTING = "Posters/Painting"
        const val GRAPHIC = "Graphic Design"
        const val PROGRAMMING = "Programming"
        const val GRAFFITI = "Graffiti"
        const val HOMEWORK = "Holidays Homework"
        const val OTHERS = "Others"
    }

    object PositiveInquiryUpdateActionLabels {
        const val CREATE_INQUIRY_AS_ADMIN = "CreateInquiryAsAdmin"
        const val REQUEST_COORDINATOR_AS_ADMIN = "RequestCoordinatorAsAdmin"
        const val REQUEST_FREELANCER_AS_COORDINATOR = "RequestFreelancerAsCoordinator"
        const val ACCEPT_INQUIRY_AS_FREELANCER = "AcceptInquiryAsFreelancer"
        const val ASSIGN_FREELANCER_AS_COORDINATOR = "AssignFreelancerAsCoordinator"
        const val MARK_RESOLVED_AS_ADMIN = "MarkResolvedAsAdmin" //
    }

    object NegativeInquiryActionLabels {
        const val DELETE_INQUIRY_AS_ADMIN = "DeleteInquiryAsAdmin" // must be done by an admin
        const val REJECT_INQUIRY_AS_COORDINATOR = "RejectInquiryAsCoordinator"  // must be done by a coordinator
        const val REJECT_INQUIRY_AS_FREELANCER = "RejectInquiryAsFreelancer" // must be done by a freelancer
    }

    val servicesList = listOf(
        Services.PROJECT,
        Services.MODELS,
        Services.ACADEMIC_WRITING,
        Services.MS_OFFICE,
        Services.DIY,
        Services.PAINTING,
        Services.GRAPHIC,
        Services.PROGRAMMING,
        Services.GRAFFITI,
        Services.HOMEWORK,
        Services.OTHERS
    )

}