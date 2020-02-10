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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plutonem.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.plutonem.PlutonemCorderActivity

/**
 * Fragment to hold a list of all shop [PlutonemCdetailAttribute]s.
 */
class PlutonemCdetailAttributeFragment : BottomSheetDialogFragment(), PlutonemCdetailAttributeAdapter.PlutonemCdetailAttributeAdapterListener {

    private var chipGroupIsChecked : Boolean = false

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cdetail_attribute, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceBundle: Bundle?) {
        super.onViewCreated(view, savedInstanceBundle)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val materialButton: MaterialButton = view.findViewById(R.id.material_button)
        val adapter = PlutonemCdetailAttributeAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.submitList(PlutonemCdetailAttribute.values().toList())

        materialButton.setOnClickListener {
            if (!chipGroupIsChecked) {
                Toast.makeText(view.context, "请选择 尺码", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(view.context, PlutonemCorderActivity::class.java))
            }
        }
    }

    override fun onShowBottomSheetClicked() {
//        BottomSheetFragment().show(requireFragmentManager(), BottomSheetFragment.FRAGMENT_TAG)
    }

    override fun onChipGroupStatedChanged(checkId : Int) {
        chipGroupIsChecked = checkId != View.NO_ID
    }
}