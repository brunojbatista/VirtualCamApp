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
import java.io.File

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

        val firestore = FirebaseFirestore.getInstance()

        val userRef = firestore.collection("Users").document(userId)
        val userPlanRef = userRef.collection("UsersPlans").document(planId)

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
                    showSnackbar(
                        message = "Plano ativado para ${user.name}",
                        actionText = "DESFAZER"
                    ) {
                        //desfazerAtivacao(userId, planId)
                        Log.d("AdminActivity", "Desfazendo o plano para o usuário selecionado: ${user.name}")
                    }
                }.addOnFailureListener { e ->
                    showSnackbar("Erro ao atualizar usuário: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                showSnackbar("Erro ao ativar plano: ${e.message}")
            }

        /*val userRef = firestore.collection("Users").document(user.id!!)

        userRef.update("planId", user.requestPlanId)
            .addOnSuccessListener {
                showSnackbar(
                    message = "Plano ativado para ${user.name ?: "Sem Nome"}",
                    actionText = "DESFAZER"
                ) {
                    // Undo → remove o plano e reinjeta o item na lista
                    userRef.update("planId", null).addOnSuccessListener {
                        val novaLista = adapter.currentList.toMutableList()
                        novaLista.add(0, user)
                        adapter.submitList(novaLista)
                        binding.recyclerView.scrollToPosition(0)
                    }
                }
            }*/
    }
}