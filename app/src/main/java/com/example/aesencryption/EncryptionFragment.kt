package com.example.aesencryption

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.aesencryption.databinding.FragmentEncryptionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket

class EncryptionFragment : Fragment() {
    private val aes = AesWrapper.getInstance()
    private lateinit var encryptDir : String
    private lateinit var decryptDir : String
    private var _binding : FragmentEncryptionBinding? = null
    private val binding get() =  _binding!!
    private val address = "localhost"
    private val port = 50000
    private val secretKeySize = listOf("128","192","256")

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
                    val cipherText = async(Dispatchers.Default) {
                        aes.encrypt(inputEditText.text.toString())
                    }.await()
                    output.setText(cipherText)
                }
            }
            send.setOnClickListener{
                if(output.text?.isEmpty() == false){
                    CoroutineScope(Dispatchers.IO).launch {
                        val socket = Socket()
                        try{
                            socket.connect(
                                InetSocketAddress(address,port)
                            )
                            if (socket.isConnected) {
                                val writer = PrintWriter(socket.getOutputStream(),true)
                                writer.println(AesWrapper.getSecreteKey())
                                writer.println(AesWrapper.getIV())
                                writer.println(output.text)
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(requireContext(),"Sent",Toast.LENGTH_SHORT).show()
                                }
                            }
                            else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Connect failed",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }catch (ex : Exception){
                            ex.printStackTrace()
                        }
                        finally {
                            socket.close()
                        }
                    }
                }
                else{
                    Toast.makeText(requireContext(),"Cipher text field is empty",Toast.LENGTH_SHORT).show()
                }
            }
            openFolder.setOnClickListener{
                FileChooserBottomSheetFragment(ENCRYPT_DIR){ fileName ->
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