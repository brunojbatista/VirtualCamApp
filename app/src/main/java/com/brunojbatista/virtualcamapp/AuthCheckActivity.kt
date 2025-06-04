package com.brunojbatista.virtualcamapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.brunojbatista.virtualcamapp.model.Users
import com.brunojbatista.virtualcamapp.utils.navigateTo
import com.brunojbatista.virtualcamapp.utils.showMessage
import com.brunojbatista.virtualcamapp.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AuthCheckActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        val appCheck = FirebaseAppCheck.getInstance()
        val providerFactory = if (BuildConfig.IS_DEBUG) {
            Log.d("AppCheck", "Usando DebugAppCheckProviderFactory")
            DebugAppCheckProviderFactory.getInstance()
        } else {
            Log.d("AppCheck", "Usando PlayIntegrityAppCheckProviderFactory")
            PlayIntegrityAppCheckProviderFactory.getInstance()
        }
        Log.d("AppCheck", "IS_DEBUG = ${BuildConfig.IS_DEBUG}")
        appCheck.installAppCheckProviderFactory(providerFactory)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        if (BuildConfig.DEBUG) {
            try {
                val debugFactory = DebugAppCheckProviderFactory.getInstance()
                val debugClass = Class.forName("com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory")
                val method = debugClass.getDeclaredMethod("getDebugToken")
                method.isAccessible = true
                val token = method.invoke(debugFactory) as String
                Log.d("AppCheck", "Debug App Check token (manual): $token")
            } catch (e: Exception) {
                Log.e("AppCheck", "Erro ao tentar extrair token manualmente: ${e.message}")
            }
        }

        setContentView(R.layout.activity_auth_check)
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {
            val userId = firebaseAuth.currentUser?.uid
            if (userId != null) {
                firestore
                    .collection("Users")
                    .document(userId)
                    .update("loginAt", FieldValue.serverTimestamp())

                firestore
                    .collection("Users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val user = document.toObject(Users::class.java)
                            when {
                                user?.planId != null -> navigateTo<MainActivity>(clearBackStack = true)
                                user?.requestPlanId != null -> navigateTo<PurchasedPlanActivity>(clearBackStack = true)
                                else -> navigateTo<PlansActivity>(clearBackStack = true)
                            }
                        } else {
                            showMessage("Usuário não encontrado no sistema.")
                            firebaseAuth.signOut()
                            navigateTo<LoginActivity>(clearBackStack = true)
                        }
                    }
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                        showMessage("Erro ao ler dados do usuário.")
                        firebaseAuth.signOut()
                        navigateTo<LoginActivity>(clearBackStack = true)
                    }
            }
        } else {
            navigateTo<LoginActivity>(clearBackStack = true)
        }
    }
}
