package com.example.repospect.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.repospect.Database.RepoDatabase
import com.example.repospect.R
import com.example.repospect.Repository.RepoRepository
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.UI.RepoViewModelProviderFactory
import com.example.repospect.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    lateinit var viewModel : RepoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repo = RepoRepository(RepoDatabase.getDatabase(this))
        val VMPF = RepoViewModelProviderFactory(application, repo, this)
        viewModel= ViewModelProvider(this, VMPF)[RepoViewModel::class.java]
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()


        val navHostFragment=supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController=navHostFragment.navController
        setupActionBarWithNavController(navController)
    }
}