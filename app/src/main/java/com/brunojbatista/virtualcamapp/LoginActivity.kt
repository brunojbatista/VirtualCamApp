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
import com.brunojbatista.virtualcamapp.utils.navigateTo
import com.brunojbatista.virtualcamapp.utils.showMessage
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
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
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Tratar erro
                            exception.printStackTrace()
                            showMessage("Ocorreu um leitura do usuário.")
                            firebaseAuth.signOut()
                        }
                } else {
                    showMessage("Usuário não encontrado no sistema.")
                    firebaseAuth.signOut()
                }
            }
            .addOnFailureListener { error ->
                try {
                    throw error
                } catch (e: FirebaseAuthInvalidUserException) {
                    showMessage("Email ou senha inválido.")
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    showMessage("Email ou senha inválido.")
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
