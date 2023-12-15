package com.example.repospect.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.repospect.Activities.LoginActivity
import com.example.repospect.Activities.MainActivity
import com.example.repospect.R
import com.example.repospect.UI.AuthViewModel
import com.example.repospect.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private lateinit var authViewModel: AuthViewModel
    private var _binding: FragmentProfileBinding? = null
    private val binding get()=_binding!!
    private var popupWindow: PopupWindow? = null
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = (activity as MainActivity).authViewModel
        if(authViewModel.isLogin()) {
            setImage()
            binding.apply {
                nameTv.text = authViewModel.getUserName()
                emailTv.text = authViewModel.getEmail()
                savedRepoDataCard.dataCardTitle.text="Saved Repositories"
                savedRepoDataCard.dataCardBody.text = "-"
            }
        }
        else{
            showSnackbar("Something went wrong")
        }

        binding.changeNameLl.setOnClickListener {
            if(authViewModel.hasInternetConnection()){

            }
            else{
                showSnackbar("Internet not available")
            }
        }

        binding.editImageBtn.setOnClickListener {
            if(authViewModel.hasInternetConnection()){
                openGallery()
            }
            else{
                showSnackbar("Internet not available")
            }
        }

        binding.signoutLl.setOnClickListener {
            signOut()
        }

        binding.changePasswordLl.setOnClickListener {
            showChangePasswordPopup()
        }
        binding.changeNameLl.setOnClickListener {
            showChangeNamePopup()
        }
        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
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
            uploadImage(data.data!!){
                updateUserImage(it)
            }
        }
    }

    private fun setImage(){
        Glide.with(requireContext()).load(authViewModel.getUserImage()).into(binding.userImage)
    }
    private fun updateUserImage(image: String){
        authViewModel.updateProfilePic(authViewModel.getEmail(), image){res, message ->
            if(res==null){
                showSnackbar("Error: $message")
            }
            else{
                authViewModel.updateImage(image)
                setImage()
            }
        }
    }
    private fun uploadImage(uri: Uri, callback: (String)-> Unit){
        val imageRef = storageRef.child("images/${uri.lastPathSegment}")
        imageRef.putFile(uri)
            .addOnSuccessListener {
                imageRef.downloadUrl
                    .addOnSuccessListener {
                        callback(it.toString())
                    }
                    .addOnFailureListener {
                        showSnackbar("Something went wrong...")
                    }
            }
            .addOnFailureListener {
                showSnackbar("Couldn't upload image")
            }
    }

    private fun showChangePasswordPopup(){
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.change_password_popup, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(popupView)

        val newPasswordEdtxt: EditText = popupView.findViewById(R.id.new_password_edtxt)
        val newPasswordEdtxt2: EditText = popupView.findViewById(R.id.new_password_edtxt2)
        val currentPassword : EditText = popupView.findViewById(R.id.curr_password_edtxt)
        val cancelBtn: Button = popupView.findViewById(R.id.cancel_btn)
        val submitBtn: Button = popupView.findViewById(R.id.change_password_button_popup)

        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setOnShowListener {
            val windowParams = WindowManager.LayoutParams()
            windowParams.copyFrom(alertDialog.window?.attributes)
            windowParams.dimAmount = 1f
            alertDialog.window?.attributes = windowParams
        }
        submitBtn.setOnClickListener {
            if(newPasswordEdtxt.text.toString()!=newPasswordEdtxt2.text.toString()){
                showSnackbar("Passwords don't match!")
                newPasswordEdtxt.text.clear()
                newPasswordEdtxt2.text.clear()
            }
            else{
                authViewModel.changePassword(authViewModel.getEmail(), currentPassword.text.toString(), newPasswordEdtxt.text.toString()){ res, message ->
                    if(res==null){
                        showSnackbar(message!!)
                    }
                    else{
                        authViewModel.createSession(res)
                        showSnackbar("Password changed successfully")
                    }
                    alertDialog.dismiss()
                }
            }
        }
        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
    private fun showChangeNamePopup(){
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.change_name_popup, null)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(popupView)

        val nameEditText: EditText = popupView.findViewById(R.id.name_textedit)
        val passwordEditText: EditText = popupView.findViewById(R.id.password_textedit)
        val submitBtn: Button = popupView.findViewById(R.id.change_name_button_popup)
        val cancelBtn: Button = popupView.findViewById(R.id.cancel_btn)

        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setOnShowListener {
            val windowParams = WindowManager.LayoutParams()
            windowParams.copyFrom(alertDialog.window?.attributes)
            windowParams.dimAmount = 1f
            alertDialog.window?.attributes = windowParams
        }
        submitBtn.setOnClickListener {
            if(nameEditText.text.toString().isNullOrEmpty()){
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
            else{
                if(passwordEditText.text.toString().isNullOrEmpty()){
                    Toast.makeText(requireContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show()
                }
                else{
                    authViewModel.changeUsername(authViewModel.getEmail(), nameEditText.text.toString(), passwordEditText.text.toString()){ res, message ->
                        if(res==null){
                            showSnackbar(message!!)
                        }
                        else{
                            showSnackbar("Username changed successfully.")
                            authViewModel.createSession(res)
                        }
                        alertDialog.dismiss()
                    }
                }
            }
        }
        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun signOut() {
        authViewModel.signOut()
        val intent = Intent(
            requireContext(),
            LoginActivity::class.java
        )
        startActivity(intent)
    }
    private fun showSnackbar(message: String){
        activity?.let { Snackbar.make(requireContext(), it.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show() }
    }

}
