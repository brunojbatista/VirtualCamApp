package com.brunojbatista.virtualcamapp.model

import java.util.Date

data class UsersPlans(
    var id: String? = null,
    var userId: String? = null,
    var totalDays: Int? = null,
    var startAt: Date? = null,

    var updatedAt: Date? = null,
    var createdAt: Date? = null,
)
