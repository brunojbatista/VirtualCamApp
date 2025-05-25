package com.brunojbatista.virtualcamapp.model

import java.util.Date

data class UsersPlans(
    var id: String,
    var userId: String,
    var totalDays: Int,
    var start: Date? = null,

    var updatedAt: Date? = null,
    var createdAt: Date = Date(),
)
