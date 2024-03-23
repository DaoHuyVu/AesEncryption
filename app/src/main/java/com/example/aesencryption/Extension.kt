package com.example.aesencryption


import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import java.io.File

fun Fragment.createArrayAdapter(array : List<String>) : ArrayAdapter<String> {
    return ArrayAdapter(
        requireContext(),
        android.R.layout.simple_spinner_item,
        array
    ).also{
        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }
}
fun spinnerListener(
    action : (Int) -> Unit
) : AdapterView.OnItemSelectedListener{
    return object : AdapterView.OnItemSelectedListener{
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            if(position != 0){
                action.invoke(position)
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}
fun Fragment.getFileContent(directory : String,fileName : String) : String{
    return File("${requireContext().filesDir}/$directory",fileName).readText()
}
fun Char.fromHexToInt() : Int {
    return if(this in '0' .. '9') this.code - '0'.code
    else this.code - 'a'.code + 10
}