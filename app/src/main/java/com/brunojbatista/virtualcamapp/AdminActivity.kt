package com.brunojbatista.virtualcamapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.brunojbatista.virtualcamapp.databinding.ActivityAdminBinding
import com.brunojbatista.virtualcamapp.utils.initializeToolbarUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAdminBinding.inflate( layoutInflater )
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
        setContentView( binding.root )
        initializeToolbarUtil(binding.includeAdminToolbar, firebaseAuth)
    }
}