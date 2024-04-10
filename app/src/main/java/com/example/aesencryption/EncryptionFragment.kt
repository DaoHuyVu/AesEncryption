package com.example.aesencryption

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.aesencryption.databinding.FragmentEncryptionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.File

class EncryptionFragment : Fragment() {
    private val aes = AesWrapper.getInstance()
    private lateinit var encryptDir : String
    private lateinit var decryptDir : String
    private var _binding : FragmentEncryptionBinding? = null
    private val binding get() =  _binding!!
    private val secretKeySize = listOf("128","192","256")
    private var pickedFile: String? = null
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
        val keySizeAdapter = createArrayAdapter(secretKeySize)
        binding.apply{
            keySizePicker.adapter = keySizeAdapter
            keySizePicker.onItemSelectedListener = spinnerListener{
                aes.setKeySize(secretKeySize[it].toInt())
            }
            encryptButton.setOnClickListener{
                CoroutineScope(Dispatchers.Main).launch{
                    val start = System.currentTimeMillis()
                    val cipherText = async(Dispatchers.Default) {
                        aes.encrypt(inputEditText.text.toString())
                    }.await()
                    output.setText(cipherText)
                    timeTaken.text = (System.currentTimeMillis()-start).toString()
                }
            }
            save.setOnClickListener{
                if(pickedFile != null){
                    File("${requireContext().filesDir}/$decryptDir","$pickedFile-encrypt").apply {
                        if(exists()){
                            delete()
                        }
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
            openFolder.setOnClickListener{
                FileChooserBottomSheetFragment(ENCRYPT_DIR){ fileName ->
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