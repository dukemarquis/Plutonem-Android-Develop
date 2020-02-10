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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.plutonem.R
import com.plutonem.add_delivery_information.AddDeliveryInformation.TEXT_FIELD

/**
 * Sealed class to define all [RecyclerView.ViewHolder]s used to display [AddDeliveryInformation]s.
 */
sealed class AddDeliveryInformationViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    open fun bind(component: AddDeliveryInformation) {
        // Override in subclass if needed.
    }

    class TextFieldComponentViewHolder(
            parent: ViewGroup
    ) : AddDeliveryInformationViewHolder(inflate(parent, R.layout.add_delivery_information_detail))

    companion object {
        fun create(
                parent: ViewGroup,
                viewType: Int
        ): AddDeliveryInformationViewHolder {
            return when (AddDeliveryInformation.values()[viewType]) {
                TEXT_FIELD -> TextFieldComponentViewHolder(parent)
            }
        }

        private fun inflate(parent: ViewGroup, layout: Int): View {
            return LayoutInflater.from(parent.context).inflate(layout, parent, false)
        }
    }
}