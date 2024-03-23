package com.example.aesencryption

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.aesencryption.databinding.FragmentDecryptionBinding
import com.example.aesencryption.databinding.FragmentEncryptionBinding
import java.io.File

class DecryptionFragment : Fragment() {
    private val aes = AesWrapper.getInstance()
    private lateinit var encryptDir : String
    private lateinit var decryptDir : String
    private var _binding : FragmentDecryptionBinding? = null
    private val binding get() =  _binding!!
    private val secretKeySize = listOf("128","192","256")
    private lateinit var pickedFile : String
    private val files = mutableListOf("Choose a file")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDecryptionBinding.inflate(layoutInflater,container,false)
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
        val filesDir = File(context?.filesDir,decryptDir)
        filesDir.listFiles()?.map{ file ->
            files.add(file.name)
        }?.toList()

        val keySizeAdapter = createArrayAdapter(secretKeySize)
        val fileAdapter = createArrayAdapter(files)
        binding.apply{
            keySizePicker.adapter = keySizeAdapter
            keySizePicker.onItemSelectedListener = spinnerListener{
                aes.setKeySize(secretKeySize[it].toInt())
            }
            filePicker.onItemSelectedListener = spinnerListener {
                pickedFile = files[it]
                inputEditText.setText(getFileContent(decryptDir,pickedFile))
            }
            filePicker.adapter = fileAdapter
            encryptButton.setOnClickListener{
                val start = System.currentTimeMillis()
                output.setText(aes.decrypt(inputEditText.text.toString()))
                timeTaken.text = (System.currentTimeMillis()-start).toString()
            }
            save.setOnClickListener{
                File("${requireContext().filesDir}/$encryptDir","$pickedFile-decrypt").apply {
                    if(exists()){
                        delete()
                    }
                    if(createNewFile()){
                        writeText(output.text.toString())
                        Toast.makeText(requireContext(),"Added to Encrypt directory", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(requireContext(),"Add failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}