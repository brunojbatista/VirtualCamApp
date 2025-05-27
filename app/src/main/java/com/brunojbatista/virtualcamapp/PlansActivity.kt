package com.brunojbatista.virtualcamapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import com.brunojbatista.virtualcamapp.databinding.ActivityLoginBinding
import com.brunojbatista.virtualcamapp.databinding.ActivityPlansBinding
import com.brunojbatista.virtualcamapp.utils.applyDefaultStyle
import com.brunojbatista.virtualcamapp.utils.initializeToolbarUtil
import com.brunojbatista.virtualcamapp.utils.navigateTo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.content.res.ColorStateList
import com.brunojbatista.virtualcamapp.model.UsersPlans
import com.brunojbatista.virtualcamapp.utils.showMessage
import com.google.firebase.firestore.FieldValue
import java.util.Date

class PlansActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPlansBinding.inflate( layoutInflater )
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private var days: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        // initializeToolbar()
        initializeToolbarUtil(binding.includePlansToolbar, firebaseAuth)
        initializeSlider()
        initializeEvents()
    }

    private fun initializeEvents() {
        // Processo de comprar o plano
        binding.buttonBuyPlan.setOnClickListener {
            buyPlan()
        }
    }

    private fun buyPlan() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userPlan = UsersPlans(null, userId, days)
            firestore
                .collection("Users")
                .document(userId)
                .collection("UsersPlans")
                .add(userPlan)
                .addOnSuccessListener { documentRef ->
                    firestore
                        .collection("Users")
                        .document(userId)
                        .collection("UsersPlans")
                        .document(documentRef.id)
                        .update(
                            mapOf(
                                "id" to documentRef.id,
                                "createdAt" to FieldValue.serverTimestamp(),
                            )
                        )
                    firestore
                        .collection("Users")
                        .document(userId)
                        .update(
                            mapOf(
                                "requestPlanId" to documentRef.id,
                                "updatedAt" to FieldValue.serverTimestamp(),
                            )
                        )
                    showMessage("Plano solicitado com sucesso")
                    navigateTo<PurchasedPlanActivity>(clearBackStack = true)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    showMessage("Ocorreu um erro na solicitação do plano")
                }
        }
    }

    private fun initializeSlider() {
        val sliderDays = binding.sliderDays
        val textDaysSelected = binding.textDaysSelected

        val blue = ContextCompat.getColor(this, R.color.blue_app)

        sliderDays.trackActiveTintList = ColorStateList.valueOf(blue)
        sliderDays.thumbTintList = ColorStateList.valueOf(blue)

        sliderDays.setLabelFormatter { value ->
            val intVal = value.toInt()
            if (intVal == 1) "1 dia" else "$intVal dias"
        }

        sliderDays.addOnChangeListener { _, value, _ ->
            days = value.toInt()
            textDaysSelected.text = "Você selecionou: $days dia(s)"
        }
    }


}