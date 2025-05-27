package com.brunojbatista.virtualcamapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.brunojbatista.virtualcamapp.databinding.ActivityPlansBinding
import com.brunojbatista.virtualcamapp.databinding.ActivityPurchasedPlanBinding
import com.brunojbatista.virtualcamapp.model.Users
import com.brunojbatista.virtualcamapp.model.UsersPlans
import com.brunojbatista.virtualcamapp.utils.initializeToolbarUtil
import com.brunojbatista.virtualcamapp.utils.navigateTo
import com.brunojbatista.virtualcamapp.utils.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import android.content.ActivityNotFoundException

class PurchasedPlanActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPurchasedPlanBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private var user: Users? = Users()
    private var userPlan: UsersPlans? = UsersPlans()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initializeToolbarUtil(binding.includePurchasedPlansToolbar, firebaseAuth)
        initializeEvents()
    }

    override fun onStart() {
        super.onStart()
        readRequestPlan()
    }

    private fun initializeEvents() {
        binding.buttonCommunicate.setOnClickListener {
            val name = user?.name
            val email = user?.email
            val days = userPlan?.totalDays
            val message = generateText(name!!, email!!, days!!)
            shareOnWhatsApp(message)
        }
    }

    private fun readRequestPlan() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            showMessage("Usuário não autenticado.")
            navigateTo<LoginActivity>(clearBackStack = true)
            return
        }

        // Busca o documento do usuário
        firestore.collection("Users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    user = document.toObject(Users::class.java)

                    when {
                        user?.planId != null -> {
                            // Usuário já tem plano ativo
                            navigateTo<AppActivity>(clearBackStack = true)
                        }
                        user?.requestPlanId == null -> {
                            // Usuário ainda não solicitou nenhum plano
                            navigateTo<PlansActivity>(clearBackStack = true)
                        }
                        else -> {
                            // Buscar o plano solicitado na subcoleção UsersPlans
                            val requestPlanId = user?.requestPlanId
                            if (requestPlanId != null) {
                                firestore.collection("Users")
                                    .document(userId)
                                    .collection("UsersPlans")
                                    .document(requestPlanId)
                                    .get()
                                    .addOnSuccessListener { planDoc ->
                                        if (planDoc.exists()) {
                                            userPlan = planDoc.toObject(UsersPlans::class.java)
                                            val totalDays = userPlan?.totalDays
                                            binding.textPlan1Description.text =
                                                "Você já requisitou um plano de $totalDays dia(s)."
                                        } else {
                                            showMessage("Plano solicitado não encontrado.")
                                        }
                                    }
                                    .addOnFailureListener {
                                        showMessage("Ocorreu um erro na leitura do plano do usuário.")
                                    }
                            }
                        }
                    }
                } else {
                    showMessage("Usuário não encontrado no sistema.")
                    firebaseAuth.signOut()
                    navigateTo<LoginActivity>(clearBackStack = true)
                }
            }
            .addOnFailureListener {
                showMessage("Erro ao acessar os dados do usuário.")
            }
    }

    private fun generateText(nome: String, email: String, dias: Int): String {
        return """
        Olá! Gostaria de solicitar a ativação de um plano do VirtualCamApp.

        Nome: $nome
        Email: $email
        Total de dias solicitados: $dias

        Aguardo retorno. Obrigado!
    """.trimIndent()
    }

    private fun shareOnWhatsApp(message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showMessage("WhatsApp não está instalado.")
        }
    }


}