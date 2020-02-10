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

package com.plutonem.add_delivery_information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.plutonem.R
import com.plutonem.databinding.FragmentAddDeliveryInformationBinding
import com.plutonem.utilities.InjectorUtils
import com.plutonem.viewmodels.AddDeliveryInformationViewModel

/**
 * Fragment to hold a list of all shop [AddDeliveryInformation]s.
 */
class AddDeliveryInformationFragment : Fragment() {

    private val addDeliveryInformationViewModel: AddDeliveryInformationViewModel by viewModels {
        InjectorUtils.provideAddDeliveryInformationViewModelFactory(requireActivity())
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentAddDeliveryInformationBinding>(
                inflater, R.layout.fragment_add_delivery_information, container, false
        ).apply {
            val adapter = AddDeliveryInformationAdapter()
            recyclerView.adapter = adapter
            adapter.submitList(AddDeliveryInformation.values().toList())

            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_save -> {
                        saveDeliveryInformation(recyclerView.getChildAt(0))
                        true
                    }
                    else -> false
                }
            }
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    // Helper function for calling a save functionality.
    // Should be used when user presses a save button/menu item.
    @Suppress("DEPRECATION")
    private fun saveDeliveryInformation(view: View) {
        val consigneeEditText = view.findViewById<TextInputEditText>(R.id.consignee_edit_text).text.toString()
        val cellPhoneNumberEditText = view.findViewById<TextInputEditText>(R.id.cell_phone_number_edit_text).text.toString()
        val detailedAddressEditText = view.findViewById<TextInputEditText>(R.id.detailed_address_edit_text).text.toString()

        when {
            consigneeEditText.length < 2 || consigneeEditText.length > 25 -> MaterialAlertDialogBuilder(context)
                    .setMessage(R.string.consignee_alert)
                    .setPositiveButton(R.string.check, null)
                    .show()
            cellPhoneNumberEditText.length != 11 -> MaterialAlertDialogBuilder(context)
                    .setMessage(R.string.cell_phone_number_alert)
                    .setPositiveButton(R.string.check, null)
                    .show()
            detailedAddressEditText.length < 5 || consigneeEditText.length > 120 -> MaterialAlertDialogBuilder(context)
                    .setMessage(R.string.detailed_address_alert)
                    .setPositiveButton(R.string.check, null)
                    .show()
            else -> {
                addDeliveryInformationViewModel.deleteAllDeliveryInformation()
                addDeliveryInformationViewModel.addDeliveryInformation(consigneeEditText, cellPhoneNumberEditText, detailedAddressEditText)
                view.findNavController().navigateUp()
            }
        }
    }

//    override fun onViewCreated(view: View, savedInstanceBundle: Bundle?) {
//        super.onViewCreated(view, savedInstanceBundle)
//
//
//    }
}