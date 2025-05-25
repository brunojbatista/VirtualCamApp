package com.brunojbatista.virtualcamapp.model

import java.util.Date

data class Users(
    var id: String,
    var name: String,
    var email: String,
    var profilePhotoPath: String? = null,
    var planId: String? = null,

    var updatedAt: Date? = null,
    var createdAt: Date = Date(),
)
