package com.zerodeg.chatrecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.zerodeg.chatrecyclerview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    lateinit var binding: ActivityMainBinding
    lateinit var navController : NavController
    private lateinit var navHostFragment : NavHostFragment
    private var currentFragment: Int = 0

    init {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        navController = binding..findNavController()

        navHostFragment = supportFragmentManager.findFragmentById(R.id.loginFragment) as NavHostFragment
        navController = navHostFragment.navController
        currentFragment = navController.currentDestination!!.id

    }

}