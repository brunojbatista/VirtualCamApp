package com.brunojbatista.virtualcamapp.utils

import android.app.Activity
import android.widget.Toast

fun Activity.showMessage(msg: String) {
    Toast.makeText(
        this,
        msg,
        Toast.LENGTH_LONG
    ).show()
}