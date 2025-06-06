package com.brunojbatista.virtualcamapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.brunojbatista.virtualcamapp.model.UserRequest

object UserRequestDiffCallback : DiffUtil.ItemCallback<UserRequest>() {
    override fun areItemsTheSame(oldItem: UserRequest, newItem: UserRequest): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserRequest, newItem: UserRequest): Boolean {
        return oldItem == newItem
    }
}
