/*
 * Copyright 2019 The Plutonem Application Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.plutonem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.plutonem.ConfirmOrderFragmentDirections
import com.plutonem.R
import com.plutonem.data.DeliveryInformation
import com.plutonem.databinding.ConfirmOrderBinding
import com.plutonem.viewmodels.ConfirmOrderViewModel

class ConfirmOrderAdapter :
        ListAdapter<DeliveryInformation, ConfirmOrderAdapter.ViewHolder>(
                ConfirmOrderDiffCallback()
        ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.confirm_order, parent, false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
            private val binding: ConfirmOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener { view ->
                navigateToAddDeliveryInformation(view)
            }
        }

        private fun navigateToAddDeliveryInformation(view: View) {
            val direction = ConfirmOrderFragmentDirections
                    .actionConfirmOrderFragmentToAddDeliveryInformationFragment()
            view.findNavController().navigate(direction)
        }

        fun bind(deliveryInformation: DeliveryInformation) {
            with(binding) {
                viewModel = ConfirmOrderViewModel(deliveryInformation)
                executePendingBindings()
            }
        }
    }
}

private class ConfirmOrderDiffCallback : DiffUtil.ItemCallback<DeliveryInformation>() {

    override fun areItemsTheSame(
            oldItem: DeliveryInformation,
            newItem: DeliveryInformation
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
            oldItem: DeliveryInformation,
            newItem: DeliveryInformation
    ): Boolean {
        return oldItem == newItem
    }
}