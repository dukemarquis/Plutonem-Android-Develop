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

package com.plutonem

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.plutonem.adapter.ConfirmOrderAdapter
import com.plutonem.databinding.FragmentConfirmOrderBinding
import com.plutonem.utilities.InjectorUtils
import com.plutonem.viewmodels.DeliveryInformationViewModel
import android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS
import android.content.Intent


class ConfirmOrderFragment : Fragment() {

    private lateinit var binding: FragmentConfirmOrderBinding
    private lateinit var checkTheFragmentState: String

    private val viewModel: DeliveryInformationViewModel by viewModels {
        InjectorUtils.provideDeliveryInformationViewModelFactory(requireContext())
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfirmOrderBinding.inflate(inflater, container, false)
        val adapter = ConfirmOrderAdapter()
        binding.recyclerView.adapter = adapter

        subscribeUi(adapter, binding)
        return binding.root
    }

    private fun subscribeUi(adapter: ConfirmOrderAdapter, binding: FragmentConfirmOrderBinding) {
        viewModel.deliveryInformation.observe(viewLifecycleOwner) { result ->
            binding.hasDeliveryAddress = !result.isNullOrEmpty()
            if (result.isNullOrEmpty() && checkTheFragmentState == "onResume") {
                MaterialAlertDialogBuilder(context)
                        .setTitle(R.string.make_delivery_address)
                        .setMessage(R.string.ask_to_set_delivery_address)
                        .setPositiveButton(R.string.cancel_make_delivery_address, null)
                        .setNegativeButton(R.string.confirm_make_delivery_address) { dialogInterface: DialogInterface?, i: Int ->
                            run {
                                navigateToAddDeliveryInformation()
                            }
                        }
                        .show()
            } else if (!result.isNullOrEmpty()) {
                adapter.submitList(result)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkTheFragmentState = "onStart"
    }

    override fun onResume() {
        super.onResume()
        checkTheFragmentState = "onResume"
    }

    // TODO: convert to data binding if applicable
    private fun navigateToAddDeliveryInformation() {
        val direction = ConfirmOrderFragmentDirections
                .actionConfirmOrderFragmentToAddDeliveryInformationFragment()
        this.findNavController().navigate(direction)
    }
}