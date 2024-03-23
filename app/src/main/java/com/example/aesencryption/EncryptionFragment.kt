package com.example.aesencryption

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.aesencryption.databinding.FragmentEncryptionBinding
import java.io.File

class EncryptionFragment : Fragment() {
    private val aes = AesWrapper.getInstance()
    private lateinit var encryptDir : String
    private lateinit var decryptDir : String
    private var _binding : FragmentEncryptionBinding? = null
    private val binding get() =  _binding!!
    private val secretKeySize = listOf("128","192","256")
    private var pickedFile: String? = null
    private val files = mutableListOf("Choose a file")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        _binding = FragmentEncryptionBinding.inflate(layoutInflater,container,false)
        // Make Encrypt directory if it didn't exist
        encryptDir = File(context?.filesDir, ENCRYPT_DIR).apply {
            if(!exists()){
                mkdir()
            }
        }.name
        decryptDir = File(context?.filesDir, DECRYPT_DIR).apply {
            if(!exists()){
                mkdir()
            }
        }.name
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filesDir = File(context?.filesDir,encryptDir)
        filesDir.listFiles()?.map{ file ->
            files.add(file.name)
        }?.toList()
        val keySizeAdapter = createArrayAdapter(secretKeySize)
        binding.apply{
            keySizePicker.adapter = keySizeAdapter
            keySizePicker.onItemSelectedListener = spinnerListener{
                aes.setKeySize(secretKeySize[it].toInt())
            }

            encryptButton.setOnClickListener{
                val start = System.currentTimeMillis()
                output.setText(aes.encrypt(inputEditText.text.toString()))
                timeTaken.text = (System.currentTimeMillis()-start).toString()
            }
            save.setOnClickListener{
                if(pickedFile != null){
                    File("${requireContext().filesDir}/$decryptDir","$pickedFile-encrypt").apply {
                        if(createNewFile()){
                            writeText(output.text.toString())
                            Toast.makeText(requireContext(),"Added to Decrypt directory", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(requireContext(),"Add failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(requireContext(),"Pick a file first",Toast.LENGTH_SHORT).show()
                }
            }
            add.setOnClickListener{
                FileChooserBottomSheetFragment{ fileName ->
                    pickedFile = fileName
                    inputEditText.setText(getFileContent(encryptDir,fileName))
                }.show(childFragmentManager,"BottomFragment")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}