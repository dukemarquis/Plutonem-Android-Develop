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

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/**
 * An adapter to display all [PlutonemCdetailAttribute]s using their corresponding [PlutonemCdetailAttributeViewHolder].
 */
class PlutonemCdetailAttributeAdapter(
        private val listener: PlutonemCdetailAttributeAdapterListener
) : ListAdapter<PlutonemCdetailAttribute, PlutonemCdetailAttributeViewHolder>(DIFF_CALLBACK) {

    interface PlutonemCdetailAttributeAdapterListener {
        fun onShowBottomSheetClicked()
        fun onChipGroupStatedChanged(checkId : Int)
    }

    override fun getItemViewType(position: Int): Int = getItem(position).ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlutonemCdetailAttributeViewHolder {
        return PlutonemCdetailAttributeViewHolder.create(parent, viewType, listener)
    }

    override fun onBindViewHolder(holder: PlutonemCdetailAttributeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PlutonemCdetailAttribute>() {
            override fun areItemsTheSame(oldItem: PlutonemCdetailAttribute, newItem: PlutonemCdetailAttribute): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PlutonemCdetailAttribute, newItem: PlutonemCdetailAttribute): Boolean {
                return oldItem == newItem
            }
        }
    }
}