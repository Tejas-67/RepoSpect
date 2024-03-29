package com.example.repospect.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.example.repospect.Activities.LoginActivity
import com.example.repospect.Activities.MainActivity
import com.example.repospect.DataModel.UserData
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var viewModel: RepoViewModel
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private var userImageUrl: Uri = Uri.parse("")
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        viewModel=(activity as LoginActivity).viewModel
        auth=FirebaseAuth.getInstance()
        firestore=FirebaseFirestore.getInstance()
        binding.loginTxt.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
        binding.signupBtn.setOnClickListener {
            if(viewModel.hasInternetConnection()) prepareSignUp()
            else showNoInternetPopup()
        }
        binding.editBtn.setOnClickListener {
            openGallery()
        }
    }
    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            userImageUrl = data.data!!
            binding.userImage.setImageURI(userImageUrl)
        }
    }

    private fun prepareSignUp() {
        val imageRef = storageRef.child("images/${userImageUrl.lastPathSegment}")
        val uploadTask = imageRef.putFile(userImageUrl)

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                signUp(downloadUri.toString())
            }
        }.addOnFailureListener {
            Log.d("repospect", it.toString())
            showSnackbar("Error PSU: $it")
        }
    }
    private fun signUp(uri: String){
        auth.createUserWithEmailAndPassword(binding.emailTextedit.text.toString(), binding.passwordTextedit.text.toString())
            .addOnSuccessListener {
                addUserToFireStore(uri)
            }
            .addOnFailureListener {
                showSnackbar("SignUp failed: $it")
                Log.w("repospect", it.toString())
            }
    }

    private fun addUserToFireStore(uri: String) {
        firestore.collection("users").document(binding.emailTextedit.text.toString()).set(
            UserData(binding.nameTextedit.text.toString(), binding.emailTextedit.text.toString(), binding.passwordTextedit.text.toString(), uri)
        )
            .addOnSuccessListener {
                showSnackbar("SignUp successful!")
                navigateToMainActivity()
            }
            .addOnFailureListener {
                showSnackbar("Error: $it")
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
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun showSnackbar(message: String){
        activity?.let { Snackbar.make(requireContext(), it.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show() }
    }

}