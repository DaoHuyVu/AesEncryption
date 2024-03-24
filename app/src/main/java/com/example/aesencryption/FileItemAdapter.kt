package com.example.aesencryption

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aesencryption.databinding.FileItemBinding

class FileItemAdapter(
    private val delete : (Int,String) -> Unit,
    private val choose : (String) -> Unit
) : ListAdapter<String, FileItemAdapter.FileItemViewHolder>(fileItemDiffer){

    class FileItemViewHolder(
        private val binding : FileItemBinding
    ) : RecyclerView.ViewHolder(binding.root){
            companion object{
                fun createViewHolder(parent : ViewGroup) : FileItemViewHolder{
                    val binding =
                        FileItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    return FileItemViewHolder(binding)
                }
            }
        fun bind(fileName : String,delete : (Int,String) -> Unit,choose : (String) -> Unit,position : Int){
            binding.fileName.text = fileName
            binding.deleteButton.setOnClickListener {
                delete.invoke(position,fileName)
            }
            binding.root.setOnClickListener {
                choose.invoke(fileName)
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) = FileItemViewHolder.createViewHolder(parent)

    override fun onBindViewHolder(holder: FileItemViewHolder, position: Int) {
        holder.bind(getItem(position),delete,choose,position)
    }
}
private val fileItemDiffer = object : DiffUtil.ItemCallback<String>(){
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}