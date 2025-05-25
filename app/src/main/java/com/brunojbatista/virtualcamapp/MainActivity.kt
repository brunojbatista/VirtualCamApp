package com.brunojbatista.virtualcamapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //Inicialização do App Check do Firebase
        FirebaseApp.initializeApp(this)
        //val factory = PlayIntegrityAppCheckProviderFactory.getInstance()
        val factory = DebugAppCheckProviderFactory.getInstance()
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(factory)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth.signOut()
    }

    override fun onStart() {
        super.onStart()

        var intent: Intent? = null
        if (firebaseAuth.currentUser != null) {
            // Fazer uma verificação se tem algum plano já existente
            // para mandar para a tela principal do app
            // caso não tenha manda para a tela de pagamentos


            // Ir para a tela de pagamento por padrão, caso usuário logado não tenha plano
            intent = Intent(applicationContext, PaymentActivity::class.java)

            // Verificar se tem algum plano ativo
            // intent = Intent(applicationContext, PaymentActivity::class.java)
        } else {
            // Caso usuário deslogado, mande para a tela de login
            intent = Intent(applicationContext, LoginActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
