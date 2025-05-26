package com.brunojbatista.virtualcamapp.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.brunojbatista.virtualcamapp.R

fun MaterialToolbar.applyDefaultStyle(context: Context) {
    // Cor do ícone de overflow (três pontinhos)
    this.overflowIcon = ContextCompat.getDrawable(context, R.drawable.ic_more_vert_white_24)

    // Cor do título
    this.setTitleTextColor(ContextCompat.getColor(context, R.color.onPrimary))

    // Cor do subtítulo (caso use)
    this.setSubtitleTextColor(ContextCompat.getColor(context, R.color.onPrimary))

    // Cor do ícone de navegação (ex: seta de voltar)
    this.navigationIcon?.setTint(ContextCompat.getColor(context, R.color.white))
}