package com.example.repospect.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.repospect.Activities.LoginActivity
import com.example.repospect.Activities.MainActivity
import com.example.repospect.DataModel.Repo
import com.example.repospect.DataModel.Resource
import com.example.repospect.DataModel.UserData
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RepoViewModel
    private lateinit var firebase: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
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
        firebase=FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()
        checkUser()
        binding.signupText.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        binding.loginBtn.setOnClickListener {
            if(viewModel.hasInternetConnection()) handleLogin()
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

    private fun checkUser() {
        if(auth.currentUser!=null) navigateToMainActivity()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun checkFields():Boolean {
        return !binding.emailLtextedit.text.isNullOrEmpty()&&!binding.passwordTextedit.text.isNullOrEmpty()
    }

    private fun handleLogin(){
        if(!checkFields()) {
            showSnackbar("Please enter email and password")
        }
        else login(binding.emailLtextedit.text.toString(), binding.passwordTextedit.text.toString())
    }
    private fun login(email: String, password: String){
        GlobalScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    navigateToMainActivity()
                }
                .addOnFailureListener {
                    showSnackbar("couldn't  login : $it")
                }
        }
    }
    private fun showSnackbar(message: String){
        activity?.let { Snackbar.make(requireContext(), it.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show() }
    }
}