package com.brunojbatista.virtualcamapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.brunojbatista.virtualcamapp.adapter.UserRequestAdapter
import com.brunojbatista.virtualcamapp.databinding.ActivityAdminBinding
import com.brunojbatista.virtualcamapp.model.UserRequest
import com.brunojbatista.virtualcamapp.utils.initializeToolbarUtil
import com.brunojbatista.virtualcamapp.utils.showSnackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.view.View

class AdminActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAdminBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var adapter: UserRequestAdapter
    private val users = mutableListOf<UserRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initializeToolbarUtil(binding.includeAdminToolbar, firebaseAuth)

        setupRecyclerView()
        loadPendingUsers()
    }

    private fun setupRecyclerView() {
        adapter = UserRequestAdapter { user ->
            activateUserPlan(user)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AdminActivity)
            itemAnimator?.apply {
                addDuration = 250
                removeDuration = 250
            }
            adapter = this@AdminActivity.adapter
        }
    }

    private fun loadPendingUsers() {
        firestore.collection("Users")
            .whereNotEqualTo("requestPlanId", null)
            .get()
            .addOnSuccessListener { result ->
                val novosUsuarios = result.map { doc ->
                    doc.toObject(UserRequest::class.java).apply { id = doc.id }
                }
                adapter.submitList(novosUsuarios)
            }
    }

    private fun activateUserPlan(user: UserRequest) {
        val userId = user.id ?: return
        val planId = user.requestPlanId ?: return

        Log.d("AdminActivity", "O usuário selecionado foi: ${user.name}")

        val userRef = firestore.collection("Users").document(userId)
        val userPlanRef = userRef.collection("UsersPlans").document(planId)

        showLoading()

        // Etapa 1: Atualiza startAt no plano solicitado
        userPlanRef.update("startAt", com.google.firebase.firestore.FieldValue.serverTimestamp())
            .addOnSuccessListener {
                // Etapa 2: Atualiza os campos do usuário
                userRef.update(
                    mapOf(
                        "planId" to planId,
                        "requestPlanId" to null
                    )
                ).addOnSuccessListener {
                    hideLoading()
                    showSnackbar(
                        message = "Plano ativado para ${user.name}",
                        actionText = "DESFAZER"
                    ) {
                        deactivateUserPlan(userId, planId, user.name)
                        Log.d("AdminActivity", "Desfazendo o plano para o usuário selecionado: ${user.name}")
                    }
                    loadPendingUsers()
                }.addOnFailureListener { e ->
                    hideLoading()
                    showSnackbar("Erro ao atualizar usuário: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                hideLoading()
                showSnackbar("Erro ao ativar plano: ${e.message}")
            }
    }

    private fun deactivateUserPlan(userId: String, planId: String, userName: String?) {
        val userRef = firestore.collection("Users").document(userId)
        val userPlanRef = userRef.collection("UsersPlans").document(planId)

        showLoading()

        // Etapa 1: Remover startAt do plano ativo
        userPlanRef.update("startAt", null)
            .addOnSuccessListener {
                // Etapa 2: Atualizar os campos do usuário
                userRef.update(
                    mapOf(
                        "requestPlanId" to planId,
                        "planId" to null
                    )
                ).addOnSuccessListener {
                    hideLoading()
                    showSnackbar("Plano desativado para $userName")
                    loadPendingUsers()
                }.addOnFailureListener { e ->
                    hideLoading()
                    showSnackbar("Erro ao atualizar usuário: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                hideLoading()
                showSnackbar("Erro ao desativar plano: ${e.message}")
            }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.alpha = 0.3f
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.alpha = 1f
    }

}