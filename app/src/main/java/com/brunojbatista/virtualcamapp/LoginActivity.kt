package com.brunojbatista.virtualcamapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.brunojbatista.virtualcamapp.databinding.ActivityLoginBinding
import com.brunojbatista.virtualcamapp.databinding.ActivitySignupBinding
import com.brunojbatista.virtualcamapp.model.Users
import com.brunojbatista.virtualcamapp.utils.showMessage
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private lateinit var email: String
    private lateinit var password: String

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

        binding.buttonLogin.setOnClickListener {
            if (readFields()) {
                signinUser(email, password)
            }
        }

    }

    private fun signinUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                showMessage("Usuário logado com sucesso.")
                // Fazer uma verificação se tem algum plano já existente
                // para mandar para a tela principal do app
                // caso não tenha manda para a tela de pagamentos
                var intent: Intent? = null

                // Ir para a tela de pagamento por padrão, caso usuário logado não tenha plano
                intent = Intent(applicationContext, PaymentActivity::class.java)

                // Verificar se tem algum plano ativo
                // intent = Intent(applicationContext, PaymentActivity::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .addOnFailureListener { error ->
                try {
                    throw error
                } catch (e: FirebaseAuthInvalidUserException) {
                    showMessage("Email ou inválido.")
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    showMessage("Email ou inválido.")
                }
            }
    }

    private fun readFields(): Boolean {
        email = binding.inputEmail.text.toString()
        password = binding.inputPassword.text.toString()
        var status = true
        status = status && ruleEmail(binding.inputEmail)
        status = status && rulePassword(binding.inputPassword)
        return status
    }

    private fun ruleEmail(field: TextInputEditText): Boolean {
        field.error = null
        if (email.isEmpty()) {
            field.error = "Preencha seu email"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            field.error = "Este email é inválido"
            return false
        }
        return true
    }

    private fun rulePassword(field: TextInputEditText): Boolean {
        field.error = null
        if (password.isEmpty()) {
            field.error = "Preencha sua senha"
            return false
        }
        val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")
        if (!passwordRegex.matches(password)) {
            field.error = "A senha deve ter ao menos 1 letra, 1 número e 6 caracteres"
            return false
        }
        return true
    }

}
