package com.renju_note.isoo.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.renju_note.isoo.R
import com.renju_note.isoo.data.StorageElement
import com.renju_note.isoo.databinding.FragmentStorageBinding
import com.renju_note.isoo.dialog.ConfirmDialog
import com.renju_note.isoo.util.StorageRVAdapter
import io.realm.Realm
import io.realm.kotlin.where

class StorageFragment : Fragment() {

    private lateinit var binding : FragmentStorageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStorageBinding.inflate(inflater, container, false)

        binding.storageListRv.layoutManager = LinearLayoutManager(requireContext())
        binding.storageListRv.adapter = StorageRVAdapter(requireContext())
        if(binding.storageListRv.itemDecorationCount == 0)
            binding.storageListRv.addItemDecoration(DividerItemDecoration(requireContext(), 1))

        val boardFragment = requireActivity().supportFragmentManager.findFragmentByTag("f0") as BoardFragment

        (binding.storageListRv.adapter as StorageRVAdapter).setOnStoragePopupMenuListener(
            object : StorageRVAdapter.StoragePopupMenuListener {
                override fun load(pos: Int, storageList: List<StorageElement>) {
                    val confirmDialog = ConfirmDialog(requireContext(), resources.getString(R.string.storage_load_confirm))
                    confirmDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    confirmDialog.setOnResponseListener(object : ConfirmDialog.OnResponseListener {
                        override fun confirm() {
                            confirmDialog.dismiss()
                            boardFragment.load(storageList[pos].getParsedUri())
                        }
                        override fun refuse() { confirmDialog.dismiss() }
                    })
                    confirmDialog.show()
                }

                override fun delete(pos: Int, element: StorageElement) {
                    val confirmDialog = ConfirmDialog(requireContext(), resources.getString(R.string.storage_delete_confirm))
                    confirmDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    confirmDialog.setOnResponseListener(object : ConfirmDialog.OnResponseListener {
                        override fun confirm() {
                            confirmDialog.dismiss()
                            val realm = Realm.getDefaultInstance()
                            val delete = realm.where<StorageElement>().equalTo("location", element.location).findFirst()
                            realm.beginTransaction()
                            delete?.deleteFromRealm()
                            realm.commitTransaction()
                            updateRV()
                        }
                        override fun refuse() { confirmDialog.dismiss() }
                    })
                    confirmDialog.show()
                }
            }
        )

        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateRV() {
        (binding.storageListRv.adapter as StorageRVAdapter).updateDataList()
        binding.storageListRv.adapter?.notifyDataSetChanged()
    }

}