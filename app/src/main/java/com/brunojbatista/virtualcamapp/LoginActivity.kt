package com.brunojbatista.virtualcamapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.brunojbatista.virtualcamapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate( layoutInflater )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )
        initializeEvents()
    }

    private fun initializeEvents() {

        // Evento do botão de cadastrar
        binding.signInHere.setOnClickListener {
            startActivity(
                Intent(this, SignupActivity::class.java)
            )
        }

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
