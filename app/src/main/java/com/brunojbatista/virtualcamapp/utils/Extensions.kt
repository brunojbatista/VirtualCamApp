package com.brunojbatista.virtualcamapp.utils

import android.app.Activity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

fun Activity.showMessage(msg: String) {
    Toast.makeText(
        this,
        msg,
        Toast.LENGTH_LONG
    ).show()
}

fun checkUserLogged(
    onLogged: (uid: String) -> Unit,
    onNotLoggedIn: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        onLogged(user.uid)
    } else {
        onNotLoggedIn()
    }
}

fun checkUserPermissionAdmin(
    onResult: (isAdmin: Boolean) -> Unit,
    onError: (Exception) -> Unit = {}
) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user == null) {
        onResult(false) // Não autenticado = não admin
        return
    }

    user.getIdToken(true)
        .addOnSuccessListener { tokenResult ->
            val isAdmin = tokenResult.claims["admin"] as? Boolean ?: false
            onResult(isAdmin)
        }
        .addOnFailureListener { exception ->
            onError(exception)
        }
}
