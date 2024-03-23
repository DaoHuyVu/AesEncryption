package com.example.aesencryption

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aesencryption.databinding.FragmentFileChooserBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

class FileChooserBottomSheetFragment(
    private val chooseOperation : (String) -> Unit
) : BottomSheetDialogFragment() {
    private var _binding : FragmentFileChooserBottomBinding? = null
    private val binding get() = _binding!!
    private var fileList = mutableListOf<String>()
    private lateinit var encryptDir : String
    private lateinit var adapter : FileItemAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileChooserBottomBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        encryptDir = File(requireContext().filesDir, ENCRYPT_DIR).apply {
            if(!exists())
                mkdir()
            listFiles()?.forEach {file ->
                fileList.add(file.name)
            }
        }.absolutePath
        adapter = FileItemAdapter(deleteOperation,chooseWrapper)
        binding.fileList.adapter = adapter
        adapter.submitList(fileList)
    }
    private val deleteOperation : (Int,String) -> Unit = { position,name ->
        File(encryptDir,name).delete()
        val fileList =  fileList.filterIndexed { idx, _ ->
            position != idx
        }
        adapter.submitList(fileList)
    }
    private val chooseWrapper : (String) -> Unit = { fileName ->
        chooseOperation.invoke(fileName)
        dismiss()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}