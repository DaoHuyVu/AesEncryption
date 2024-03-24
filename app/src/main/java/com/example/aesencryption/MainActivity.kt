package com.example.aesencryption

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.aesencryption.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var aesAdapter : FragmentStateAdapter
    private lateinit var viewPager : ViewPager2
    private val tabName = arrayOf("Encrypt","Decrypt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        aesAdapter = AesAdapter(supportFragmentManager,lifecycle)
        viewPager = binding.viewPager
        viewPager.adapter = aesAdapter
        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout,viewPager){ tab,position ->
            tab.text = tabName[position]
        }.attach()
        setSupportActionBar(binding.toolBar)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_file -> showDialogFragment()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun showDialogFragment(){
        AddFileDialogFragment().show(supportFragmentManager,"AddFileDialogFragment")
    }
    class AesAdapter(
        fragmentManager : FragmentManager,
        lifeCycle : Lifecycle
    ) : FragmentStateAdapter(fragmentManager,lifeCycle){
        override fun getItemCount(): Int {
            return 2
        }
        override fun createFragment(position: Int): Fragment {
            when(position){
                0 -> return EncryptionFragment()
                1 -> return DecryptionFragment()
            }
            throw IllegalStateException()
        }
    }
}
