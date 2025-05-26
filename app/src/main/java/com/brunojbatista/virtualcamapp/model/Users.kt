package com.brunojbatista.virtualcamapp.model

import java.util.Date

data class Users(
    var id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var profilePhotoPath: String? = null,
    var planId: String? = null,

    var updatedAt: Date? = null,
    var createdAt: Date = Date(),
)
