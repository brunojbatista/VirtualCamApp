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
import com.google.firebase.firestore.ListenerRegistration

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
    private var userListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initializeToolbarUtil(binding.includeAdminToolbar, firebaseAuth)

        setupRecyclerView()
        startListeningForUserRequests()
    }

    override fun onDestroy() {
        super.onDestroy()
        userListener?.remove()
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

    private fun startListeningForUserRequests() {
        showLoading()

        userListener = firestore.collection("Users")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    hideLoading()
                    showSnackbar("Erro ao ouvir mudanças: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshots == null || snapshots.isEmpty) {
                    hideLoading()
                    adapter.submitList(emptyList())
                    return@addSnapshotListener
                }

                val usuariosPendentes = snapshots.documents.mapNotNull { doc ->
                    val hasRequest = doc.data?.get("requestPlanId") != null
                    if (hasRequest) {
                        doc.toObject(UserRequest::class.java)?.apply { id = doc.id }
                    } else null
                }

                adapter.submitList(usuariosPendentes)
                hideLoading()
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
                }.addOnFailureListener { e ->
                    hideLoading()
                    Log.e("AdminActivity", "Erro ao ativar usuário", e)
                }
            }
            .addOnFailureListener { e ->
                hideLoading()
                Log.e("AdminActivity", "Erro ao ativar usuário", e)
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
                }.addOnFailureListener { e ->
                    hideLoading()
                    Log.e("AdminActivity", "Erro ao desativar usuário", e)
                }
            }
            .addOnFailureListener { e ->
                hideLoading()
                Log.e("AdminActivity", "Erro ao desativar usuário", e)
            }
    }

    private fun showLoading() {
        /*binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.alpha = 0.3f*/
        binding.loadingOverlay.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        /*binding.progressBar.visibility = View.GONE
        binding.recyclerView.alpha = 1f*/
        binding.loadingOverlay.visibility = View.GONE
    }

}