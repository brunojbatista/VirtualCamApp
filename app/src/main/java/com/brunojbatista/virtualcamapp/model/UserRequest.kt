package com.brunojbatista.virtualcamapp.model

data class UserRequest(
    var id: String? = null,
    var name: String? = null,
    var email: String? = null,
    var planId: String? = null,
    var requestPlanId: String? = null,
)