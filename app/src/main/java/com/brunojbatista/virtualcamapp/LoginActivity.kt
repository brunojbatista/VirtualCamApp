package com.brunojbatista.virtualcamapp

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.brunojbatista.virtualcamapp.databinding.ActivityLoginBinding
import com.brunojbatista.virtualcamapp.model.Users
import com.brunojbatista.virtualcamapp.utils.checkUserLogged
import com.brunojbatista.virtualcamapp.utils.checkUserPermissionAdmin
import com.brunojbatista.virtualcamapp.utils.navigateTo
import com.brunojbatista.virtualcamapp.utils.showMessage
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

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
            navigateTo<SignupActivity>(clearBackStack = false)
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

                checkUserLogged(
                    onLogged = { uid ->
                        firestore
                            .collection("Users")
                            .document(uid)
                            .update("loginAt", FieldValue.serverTimestamp())

                        checkUserPermissionAdmin(
                            onResult = { isAdmin ->
                                if (isAdmin) {
                                    navigateTo<MainActivity>(clearBackStack = true)
                                } else {
                                    firestore
                                        .collection("Users")
                                        .document(uid)
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
                                }
                            },
                            onError = { error ->
                                Log.e("AuthCheck", "Erro ao verificar claim admin", error)
                                showMessage("Erro ao verificar permissões")
                            }
                        )
                    },
                    onNotLoggedIn = {
                        navigateTo<LoginActivity>(clearBackStack = true)
                        showMessage("Usuário não encontrado no sistema.")
                    }
                )
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
