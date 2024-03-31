package com.pb.pb_app.utils.models.projects

enum class EnquiryStatus(val statusCode: Int) {
    Rejected(-1),
    Unassigned(0),
    PCAssignedUnaccepted(1),
    FRAssignedUnaccepted(2),
    FRAssignedAccepted(3),
    Completed(4);

    companion object {
        fun getStatusByCode(statusCode: Int): EnquiryStatus {
            return when (statusCode) {
                -1 -> Rejected
                0 -> Unassigned
                1 -> PCAssignedUnaccepted
                2 -> FRAssignedUnaccepted
                3 -> FRAssignedAccepted
                4 -> Completed
                else -> throw IllegalAccessError()
            }
        }
    }
}
