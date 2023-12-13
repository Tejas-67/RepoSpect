package com.example.repospect.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.repospect.Activities.LoginActivity
import com.example.repospect.Activities.MainActivity
import com.example.repospect.DataModel.Resource
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentLoginBinding
import com.example.repospect.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private lateinit var viewModel: RepoViewModel
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as LoginActivity).viewModel
        binding.signupBtn.setOnClickListener {
            val name = binding.nameTextedit.text.toString()
            val email = binding.emailTextedit.text.toString()
            val password = binding.passwordTextedit.text.toString()
            viewModel.signupUser(email, name, password)
        }

        binding.loginTxt.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        viewModel.currentUser.observe(viewLifecycleOwner, Observer{
            when(it){
                is Resource.Success -> {
                    moveToMainActivity()
                }
                is Resource.Error -> {
                    showSnackbar(it.message!!)
                }
                else -> {
                    showSnackbar("Loading...")
                }
            }
        })
    }
    private fun moveToMainActivity(){
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        (activity as LoginActivity).finish()
    }
    private fun showSnackbar(message: String){
        Snackbar.make(requireView(), message, 3000).show()
    }
}