package com.example.repospect.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.repospect.Activities.LoginActivity
import com.example.repospect.Activities.MainActivity
import com.example.repospect.DataModel.Resource
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RepoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as LoginActivity).viewModel
        viewModel.currentUser.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    showSnackBar("Success")
                    moveToMainActivity()
                }
                is Resource.Error -> {
                    showSnackBar("${it.message}")
                }
                else -> {
                    showSnackBar("Loading...")
                }
            }
        })

        binding.loginBtn.setOnClickListener {
            val email = binding.emailLtextedit.text.toString()
            val password = binding.passwordTextedit.text.toString()
            viewModel.loginUser(email, password)
        }

        binding.signupText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun moveToMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        (activity as LoginActivity).finish()
    }

    private fun showSnackBar(message: String){
        val snackbar = Snackbar.make(requireView(), message, 1500)
        snackbar.show()
    }
}