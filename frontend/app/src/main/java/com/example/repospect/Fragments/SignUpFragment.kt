package com.example.repospect.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
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
import com.example.repospect.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var viewModel: AuthViewModel
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private var userImageUrl: Uri = Uri.parse("")
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
        viewModel = (activity as LoginActivity).viewModel
        if(viewModel.isLogin()) navigateToMainActivity()

        binding.loginTxt.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
        binding.signupBtn.setOnClickListener {
            val email = binding.emailTextedit.text.toString()
            val name = binding.nameTextedit.text.toString()
            val password = binding.passwordTextedit.text.toString()
            if(viewModel.hasInternetConnection()){
                uploadImage {
                    if(viewModel.hasInternetConnection()) signUp(email, name, password, it.toString())
                    else showNoInternetPopup()
                }
            }
        }
        binding.editBtn.setOnClickListener {
            openGallery()
        }
    }

    private fun signUp(email: String, name: String, password: String, image: String){
        viewModel.signupUser(email, name, password, image){ res, message ->
            if(res==null) showSnackBar("Error Signing Up")
            else{
                viewModel.createSession(res)
                navigateToMainActivity()
            }
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
    private fun showSnackBar(message: String){
        Snackbar.make(requireView(), message, 1500).show()
    }

    private fun uploadImage(callback:(Uri)->Unit){
        val imageRef = storageRef.child("/image/${userImageUrl.lastPathSegment}")
        imageRef.putFile(userImageUrl)
           .addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {
                callback(it)
            }
        }
            .addOnFailureListener{
                showSnackBar("Couldn't upload image")
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

}