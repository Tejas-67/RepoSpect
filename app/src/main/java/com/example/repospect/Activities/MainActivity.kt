package com.example.repospect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.repospect.Database.RepoDatabase
import com.example.repospect.R
import com.example.repospect.Repository.RepoRepository
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.UI.RepoViewModelProviderFactory
import com.example.repospect.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get()=_binding!!

    lateinit var viewModel: RepoViewModel

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = RepoRepository(RepoDatabase.getDatabase(this))
        val VMPF = RepoViewModelProviderFactory(repo)
        viewModel=ViewModelProvider(this, VMPF).get(RepoViewModel::class.java)

        supportActionBar?.hide()

        _binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment=supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController=navHostFragment.navController
        setupActionBarWithNavController(navController)
    }
}