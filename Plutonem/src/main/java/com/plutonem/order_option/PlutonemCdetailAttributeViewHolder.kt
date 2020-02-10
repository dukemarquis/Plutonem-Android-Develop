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

package com.plutonem.order_option

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import com.plutonem.R
import com.google.android.material.button.MaterialButton
//import com.plutonem.order_option.PlutonemCdetailAttribute.BUTTON
import com.plutonem.order_option.PlutonemCdetailAttribute.CHIP
import com.plutonem.order_option.PlutonemCdetailAttribute.COUNTER
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.google.android.material.chip.ChipGroup

/**
 * Sealed class to define all [RecyclerView.ViewHolder]s used to display [PlutonemCdetailAttribute]s.
 */
sealed class PlutonemCdetailAttributeViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    open fun bind(component: PlutonemCdetailAttribute) {
        // Override in subclass if needed.
    }

//    class ButtonComponentViewHolder(
//            parent: ViewGroup
//    ) : PlutonemCdetailAttributeViewHolder(inflate(parent, R.layout.cdetail_attribute_buttons))

    class ChipComponentViewHolder(
            parent: ViewGroup,
            listener: PlutonemCdetailAttributeAdapter.PlutonemCdetailAttributeAdapterListener
    ) : PlutonemCdetailAttributeViewHolder(inflate(parent, R.layout.cdetail_attribute_chips)) {
        init {
            val chipGroup = view.findViewById<ChipGroup>(R.id.chip_group)
            chipGroup.setOnCheckedChangeListener { group, checkedId ->
                    listener.onChipGroupStatedChanged(checkedId)
            }
        }
    }

    class CounterComponentViewHolder(
            parent: ViewGroup
    ) : PlutonemCdetailAttributeViewHolder(inflate(parent, R.layout.cdetail_attribute_counters)) {
        init {
            val edit = view.findViewById<AppCompatEditText>(R.id.edit)
            val buttonIncrease = view.findViewById<MaterialButton>(R.id.button_increase)
            val buttonDecrease = view.findViewById<MaterialButton>(R.id.button_decrease)

            edit.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if (s.toString() == "") {
                        edit.setText("0")
                        edit.setSelection(1)
                        buttonDecrease.isEnabled = false
                    } else if (s.toString()[0].toString() == "0" && s.toString().length > 1) {
                        edit.setText(s.toString()[1].toString())
                        edit.setSelection(1)
                    } else buttonDecrease.isEnabled = s.toString() != "1" && s.toString() != "0"
                }
                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                }
            })
            edit.setOnKeyListener(View.OnKeyListener { view, i, keyEvent ->
                if (i == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_UP) {
                    val inputMethodManager =  view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    edit.isFocusable = false
                    edit.isFocusableInTouchMode = true
                    return@OnKeyListener true
                }
                false
            })
            buttonIncrease.setOnClickListener {
                edit.setText((edit.text.toString().toInt() + 1).toString())
            }
            buttonDecrease.setOnClickListener{
                edit.setText((edit.text.toString().toInt() - 1).toString())
            }
        }
    }

    companion object {
        fun create(
                parent: ViewGroup,
                viewType: Int,
                listener: PlutonemCdetailAttributeAdapter.PlutonemCdetailAttributeAdapterListener
        ): PlutonemCdetailAttributeViewHolder {
            return when (PlutonemCdetailAttribute.values()[viewType]) {
//                BUTTON -> PlutonemCdetailAttributeViewHolder.ButtonComponentViewHolder(parent)
                CHIP -> PlutonemCdetailAttributeViewHolder.ChipComponentViewHolder(parent, listener)
                COUNTER -> PlutonemCdetailAttributeViewHolder.CounterComponentViewHolder(parent)
            }
        }

        private fun inflate(parent: ViewGroup, layout: Int): View {
            return LayoutInflater.from(parent.context).inflate(layout, parent, false)
        }
    }
}