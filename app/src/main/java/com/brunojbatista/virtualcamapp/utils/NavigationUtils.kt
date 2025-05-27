package com.brunojbatista.virtualcamapp.utils

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

inline fun <reified T : AppCompatActivity> Context.navigateTo(clearBackStack: Boolean = false) {
    val intent = Intent(this, T::class.java)
    if (clearBackStack) {
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    startActivity(intent)
}