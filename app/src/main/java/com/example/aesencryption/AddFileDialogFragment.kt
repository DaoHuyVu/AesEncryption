package com.example.aesencryption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.aesencryption.databinding.AddFileDialogFragmentBinding
import java.io.File
class AddFileDialogFragment : DialogFragment() {
    private var _binding : AddFileDialogFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddFileDialogFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            addFile.setOnClickListener {
                val fileName = fileNameInput.text.toString()
                val fileContent = fileContentInput.text.toString()
                File("${requireContext().filesDir}/Encrypt",fileName).apply {
                    if(exists()){
                        Toast.makeText(requireContext(),"File's already exist",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        createNewFile()
                        writeBytes(fileContent.toByteArray())
                        Toast.makeText(requireContext(),"Created successfully",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}