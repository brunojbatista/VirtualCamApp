package com.brunojbatista.virtualcamapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // usa o layout que você já criou
    }

    fun onLogin(view: View) {
        // Espera 2 segundos, depois abre a tela de login
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, PaymentActivity::class.java)
            startActivity(intent)
            finish() // finaliza a splash
        }, 2000) // 2000ms = 2 segundos
    }

}
