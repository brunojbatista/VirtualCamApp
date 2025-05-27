package com.brunojbatista.virtualcamapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.brunojbatista.virtualcamapp.model.Users
import com.brunojbatista.virtualcamapp.utils.navigateTo
import com.brunojbatista.virtualcamapp.utils.showMessage
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Inicialização do App Check do Firebase
        FirebaseApp.initializeApp(this)
        //val factory = PlayIntegrityAppCheckProviderFactory.getInstance()
        val factory = DebugAppCheckProviderFactory.getInstance()
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(factory)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {
            // Fazer uma verificação se tem algum plano já existente
            // para mandar para a tela principal do app
            // caso não tenha manda para a tela de pagamentos

            // Verificar se tem algum plano ativo
            val userId = FirebaseAuth.getInstance().currentUser?.uid
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
                            if (user?.planId != null) {
                                navigateTo<AppActivity>(clearBackStack = true)
                            } else if (user?.requestPlanId != null) {
                                navigateTo<PurchasedPlanActivity>(clearBackStack = true)
                            } else {
                                navigateTo<PlansActivity>(clearBackStack = true)
                            }
                        } else {
                            // Documento não encontrado
                            showMessage("Usuário não encontrado no sistema.")
                            firebaseAuth.signOut()
                            navigateTo<LoginActivity>(clearBackStack = true)
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Tratar erro
                        exception.printStackTrace()
                        showMessage("Ocorreu um leitura do usuário.")
                        firebaseAuth.signOut()
                        navigateTo<LoginActivity>(clearBackStack = true)
                    }
            } else {
                firebaseAuth.signOut()
                navigateTo<LoginActivity>(clearBackStack = true)
            }
        } else {
            // Caso usuário deslogado, mande para a tela de login
            navigateTo<LoginActivity>(clearBackStack = true)
        }
    }
}
