package com.brunojbatista.virtualcamapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.brunojbatista.virtualcamapp.R
import com.brunojbatista.virtualcamapp.model.UserRequest

class UserRequestAdapter(
    private val onActivate: (UserRequest) -> Unit
) : ListAdapter<UserRequest, UserRequestAdapter.UserViewHolder>(UserRequestDiffCallback) {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val switchActivate: SwitchCompat = itemView.findViewById(R.id.switchActivate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_request, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)

        holder.tvName.text = user.name ?: "Sem nome"
        holder.tvEmail.text = user.email ?: "Sem email"

        holder.switchActivate.setOnCheckedChangeListener(null)
        holder.switchActivate.isChecked = false
        holder.switchActivate.isEnabled = user.requestPlanId != null

        holder.switchActivate.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                onActivate(user)
            }
        }

        // Log para verificar se está sendo chamado
        android.util.Log.d("Adapter", "Exibindo usuário: ${user.name}")
    }
}
