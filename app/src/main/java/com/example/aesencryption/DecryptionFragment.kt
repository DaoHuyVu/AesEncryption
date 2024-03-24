package com.example.aesencryption

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.aesencryption.databinding.FragmentDecryptionBinding
import java.io.File

class DecryptionFragment : Fragment() {
    private val aes = AesWrapper.getInstance()
    private lateinit var encryptDir : String
    private lateinit var decryptDir : String
    private var _binding : FragmentDecryptionBinding? = null
    private val binding get() =  _binding!!
    private var pickedFile : String? = null
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
        binding.apply{
            encryptButton.setOnClickListener{
                val start = System.currentTimeMillis()
                output.setText(aes.decrypt(inputEditText.text.toString()))
                timeTaken.text = (System.currentTimeMillis()-start).toString()
            }
            save.setOnClickListener{
                if(pickedFile != null){
                    File("${requireContext().filesDir}/$encryptDir","$pickedFile-encrypt").apply {
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
                else{
                    Toast.makeText(requireContext(),"Pick a file first",Toast.LENGTH_SHORT).show()
                }
            }
            openFolder.setOnClickListener{
                FileChooserBottomSheetFragment(DECRYPT_DIR){ fileName ->
                    pickedFile = fileName
                    inputEditText.setText(getFileContent(decryptDir,fileName))
                }.show(childFragmentManager,"BottomFragment")
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}