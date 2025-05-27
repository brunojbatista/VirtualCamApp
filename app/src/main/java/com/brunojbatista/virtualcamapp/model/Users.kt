package com.brunojbatista.virtualcamapp.model

import java.util.Date

data class Users(
    var id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var profilePhotoPath: String? = null,
    var planId: String? = null,
    var requestPlanId: String? = null,

    var loginAt: Date? = null,
    var updatedAt: Date? = null,
    var createdAt: Date? = null,
)

fun Users.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()

    this.name?.let { map["name"] = it }
    this.email?.let { map["email"] = it }
    this.profilePhotoPath?.let { map["profilePhotoPath"] = it }
    this.planId?.let { map["planId"] = it }
    this.requestPlanId?.let { map["requestPlanId"] = it }
    this.loginAt?.let { map["loginAt"] = it }
    this.updatedAt?.let { map["updatedAt"] = it }
    this.createdAt?.let { map["createdAt"] = it }

    return map
}