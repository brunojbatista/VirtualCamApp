package com.brunojbatista.virtualcamapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import com.brunojbatista.virtualcamapp.databinding.ActivityAppBinding
import com.brunojbatista.virtualcamapp.utils.applyDefaultStyle
import com.brunojbatista.virtualcamapp.utils.initializeToolbarUtil
import com.brunojbatista.virtualcamapp.utils.navigateTo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAppBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        //initializeToolbar()
        initializeToolbarUtil(binding.includeAppToolbar, firebaseAuth)
    }

    /*private fun initializeToolbar() {
        val toolbar = binding.includeAppToolbar.tbVersion1
        toolbar.applyDefaultStyle(this)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "VirtualCamApp"
        }
        addMenuProvider(
            object: MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        R.id.menuProfile -> {
                            startActivity(
                                Intent(applicationContext, ProfileActivity::class.java)
                            )
                        }
                        R.id.menuLogout -> {
                            signOutUser()
                        }
                    }
                    return true
                }
            }
        )
    }

    private fun signOutUser() {
        AlertDialog.Builder(this)
            .setTitle("Sair")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Não") { dialog, position -> }
            .setPositiveButton("Sim") { dialog, position ->
                firebaseAuth.signOut()
                navigateTo<LoginActivity>(clearBackStack = true)
            }
            .create()
            .show()
    }*/
}