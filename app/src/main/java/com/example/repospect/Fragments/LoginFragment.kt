package com.example.repospect.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import com.example.repospect.Activities.LoginActivity
import com.example.repospect.Activities.MainActivity
import com.example.repospect.R
import com.example.repospect.UI.AuthViewModel
import com.example.repospect.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        if(viewModel.isLogin()) navigateToMainActivity()

        binding.signupText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        binding.loginBtn.setOnClickListener {
            val email = binding.emailLtextedit.text.toString()
            val password = binding.passwordTextedit.text.toString()
            if(viewModel.hasInternetConnection()) {
                viewModel.loginUser(email, password){res, message->
                    if(res==null){
                        showSnackBar("Error: $message")
                    }
                    else {
                        viewModel.createSession(res)
                        navigateToMainActivity()
                    }
                }
            }
            else showNoInternetPopup()
        }

    }

    private fun showNoInternetPopup() {
        val view = layoutInflater.inflate(R.layout.no_internet_popup, null)
        val cancelButton = view.findViewById<ImageButton>(R.id.cancel_popup_btn)
        val alertDialog = AlertDialog.Builder(requireContext(), R.style.TransparentDialog).setView(view).create()
        alertDialog.show()
        cancelButton.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun showSnackBar(message: String){
        val snackbar = Snackbar.make(requireView(), message, 1500)
        snackbar.show()
    }
}