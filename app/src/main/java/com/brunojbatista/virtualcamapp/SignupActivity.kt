package com.brunojbatista.virtualcamapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.brunojbatista.virtualcamapp.databinding.ActivitySignupBinding
import com.brunojbatista.virtualcamapp.model.Users
import com.brunojbatista.virtualcamapp.utils.showMessage
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore


class SignupActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignupBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confirmPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView( binding.root )
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        initializeToolbar()
        initializeEvents()
    }

    private fun initializeEvents() {
        // Processo de cadastrar
        binding.buttonSignup.setOnClickListener {
            if (readFields()) {
                // showMessage("Cadastrar usuário...")
                signupUser(name, email, password)
            }
        }
    }

    private fun signupUser(name: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    // Adicionar um documento do usuário
                    val userId = result.result.user?.uid
                    if (userId != null) {
                        val user = Users(userId, name, email)
                        firestore
                            .collection("Users")
                            .document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                showMessage("Cadastro feito com sucesso.")
                                val intent = Intent(applicationContext, PlansActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                            .addOnFailureListener { error ->
                                error.printStackTrace()
                                showMessage("Erro ao cadastrar, reporte a equipe de suporte.")
                            }
                    }
                }
            }
            .addOnFailureListener { error ->
                try {
                    throw error
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    showMessage("Email inválido, tente outro.")
                } catch (e: FirebaseAuthUserCollisionException) {
                    showMessage("Email inválido, tente outro.")
                }
            }
    }

    private fun readFields(): Boolean {
        name = binding.inputName.text.toString()
        email = binding.inputEmail.text.toString()
        password = binding.inputPassword.text.toString()
        confirmPassword = binding.inputConfirmPassword.text.toString()
        var status = true
        status = status && ruleName()
        status = status && ruleEmail()
        status = status && rulePassword(binding.inputPassword)
        status = status && ruleConfirmPassword()
        return status
    }

    private fun ruleName(): Boolean {
        binding.inputName.error = null
        if (name.isEmpty()) {
            binding.inputName.error = "Preencha seu nome"
            return false
        }
        return true
    }

    private fun ruleEmail(): Boolean {
        binding.inputEmail.error = null
        if (email.isEmpty()) {
            binding.inputEmail.error = "Preencha seu email"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.error = "Este email é inválido"
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

    private fun ruleConfirmPassword(): Boolean {
        binding.inputConfirmPassword.error = null
        if (password != confirmPassword) {
            binding.inputConfirmPassword.error = "Confirme a senha anterior"
            return false
        }
        return rulePassword(binding.inputConfirmPassword)
    }

    private fun initializeToolbar() {
        var toolbar = binding.includeToolbar.tbVersion1
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}