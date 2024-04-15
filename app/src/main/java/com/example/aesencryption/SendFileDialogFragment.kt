package com.example.aesencryption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.aesencryption.databinding.SendFileDialogBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket

class SendFileDialogFragment(
    private val sendFile : (PrintWriter) -> Unit
) : DialogFragment() {
    private var _binding : SendFileDialogBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SendFileDialogBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            send.setOnClickListener {
                if(addressInput.text != null && portInput.text != null){
                    CoroutineScope(Dispatchers.IO).launch {
                        val socket = Socket()
                        try{
                            socket.connect(
                                InetSocketAddress(
                                    addressInput.text.toString(),
                                    portInput.text.toString().toInt()
                                )
                            )
                            if (socket.isConnected) {
                                sendFile.invoke(PrintWriter(socket.getOutputStream(),true))
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
                    Toast.makeText(requireContext(),"Fill in all information",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}