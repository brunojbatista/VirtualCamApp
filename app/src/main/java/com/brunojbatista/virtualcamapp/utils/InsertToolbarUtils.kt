package com.brunojbatista.virtualcamapp.utils

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.brunojbatista.virtualcamapp.AdminActivity
import com.brunojbatista.virtualcamapp.LoginActivity
import com.brunojbatista.virtualcamapp.MainActivity
import com.brunojbatista.virtualcamapp.ProfileActivity
import com.brunojbatista.virtualcamapp.R
import com.brunojbatista.virtualcamapp.databinding.ToolbarVersion1Binding
import com.google.firebase.auth.FirebaseAuth

fun AppCompatActivity.initializeToolbarUtil(
    binding: ToolbarVersion1Binding,
    firebaseAuth: FirebaseAuth
) {
    val toolbar = binding.tbVersion1
    // Adiciona padding para não sobrepor status bar
    ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
        val topInset = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
        view.updatePadding(top = topInset)
        insets
    }
    toolbar.applyDefaultStyle(this)
    setSupportActionBar(toolbar)
    supportActionBar?.title = "VirtualCamApp"

    // Adicionar o botão de voltar no toolbar
    val mustShowBackButton = this !is MainActivity
    supportActionBar?.setDisplayHomeAsUpEnabled(mustShowBackButton)
    if (mustShowBackButton) {
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.main_menu, menu)
            // Perfil: desabilita se já estiver na tela de perfil
            val profileItem = menu.findItem(R.id.menuProfile)
            profileItem?.isEnabled = this@initializeToolbarUtil !is ProfileActivity
            // Verifica se o usuário é admin para mostrar a opção "Administração"
            checkUserPermissionAdmin(
                onResult = { isAdmin ->
                    val adminItem = menu.findItem(R.id.menuAdmin)
                    if (isAdmin) {
                        adminItem?.isVisible = true
                        adminItem?.isEnabled = this@initializeToolbarUtil !is AdminActivity
                    } else {
                        adminItem?.isVisible = false
                    }
                },
                onError = {
                    Log.e("ToolbarMenu", "Erro ao verificar admin", it)
                }
            )
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            when (menuItem.itemId) {
                R.id.menuProfile -> {
                    navigateTo<ProfileActivity>()
                }
                R.id.menuAdmin -> {
                    navigateTo<AdminActivity>()
                    return true
                }
                R.id.menuLogout -> {
                    signOutUserUtil(this@initializeToolbarUtil, firebaseAuth)
                }
            }
            return true
        }
    })
}

fun signOutUserUtil(context: ComponentActivity, firebaseAuth: FirebaseAuth) {
    AlertDialog.Builder(context)
        .setTitle("Sair")
        .setMessage("Deseja realmente sair?")
        .setNegativeButton("Não") { dialog, _ -> dialog.dismiss() }
        .setPositiveButton("Sim") { _, _ ->
            firebaseAuth.signOut()
            context.navigateTo<LoginActivity>(clearBackStack = true)
        }
        .create()
        .show()
}
