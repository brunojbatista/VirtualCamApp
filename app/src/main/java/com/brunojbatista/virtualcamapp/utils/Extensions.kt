package com.brunojbatista.virtualcamapp.utils

import android.app.Activity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

fun Activity.showMessage(msg: String) {
    Toast.makeText(
        this,
        msg,
        Toast.LENGTH_LONG
    ).show()
}

fun Activity.showSnackbar(
    message: String,
    actionText: String? = null,
    onUndo: (() -> Unit)? = null
) {
    val rootView: View = findViewById(android.R.id.content)
    val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)

    // Se quiser ação de desfazer
    if (actionText != null && onUndo != null) {
        snackbar.setAction(actionText) {
            onUndo()
        }
        snackbar.setActionTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_light))
    }
//    snackbar.setActionTextColor(Color.YELLOW)
//    snackbar.view.setBackgroundColor(Color.DKGRAY)
    snackbar.show()
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
