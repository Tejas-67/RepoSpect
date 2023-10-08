package com.example.repospect.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.repospect.Activities.LoginActivity
import com.example.repospect.Activities.MainActivity
import com.example.repospect.DataModel.Resource
import com.example.repospect.DataModel.UserData
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentProfileBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    private lateinit var viewModel: RepoViewModel
    private var _binding: FragmentProfileBinding? = null
    private val binding get()=_binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
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
        viewModel = (activity as MainActivity).viewModel
        viewModel.currentUser.observe(viewLifecycleOwner, Observer{
            when(it){
                is Resource.Success->{
                    binding.nameTv.text = it.data!!.name
                    binding.emailTv.text = it.data.email
                    Glide.with(requireContext()).load(it.data!!.image).into(binding.userImage)
                }
                is Resource.Loading->{
                    binding.nameTv.text=""
                    binding.emailTv.text=""
                }
                else->{
                    Toast.makeText(requireContext(), "Error fetching data!", Toast.LENGTH_SHORT).show()
                }
            }
        })
        binding.savedRepoDataCard.dataCardTitle.text = "Saved Repositories"
        binding.savedRepoDataCard.dataCardBody.text = "-"
        when(viewModel.hasInternetConnection()){
            true-> {
                auth=FirebaseAuth.getInstance()
                firebase=FirebaseFirestore.getInstance()
            }
            else->{
            }
        }
        viewModel.allLocalRepo.observe(viewLifecycleOwner, Observer{
            binding.savedRepoDataCard.dataCardBody.text = "${it.size}"
        })
        binding.signoutLl.setOnClickListener {
            signOut()
        }
        binding.changeNameLl.setOnClickListener {
            showChangeNamePopup()
        }
        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }
        binding.changePasswordLl.setOnClickListener {
            showChangePasswordPopup()
        }
        binding.editImageBtn.setOnClickListener {
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
            binding.userImage.setImageURI(data.data!!)
            startImageUpdate(data.data!!)
        }
    }

    private fun startImageUpdate(uri: Uri) {
        val imageRef = storageRef.child("images/${uri.lastPathSegment}")
        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                viewModel.currentUser.value?.data?.let{
                    updateUserDetails(it.name, it.email, it.password, downloadUri.toString(), null)
                }
            }
        }.addOnFailureListener {
            Log.d("repospect", it.toString())
            showSnackbar("Error: $it")
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
                if(currentPassword.text.toString()==viewModel.currentUser.value!!.data!!.password){
                    changePassword(
                        alertDialog,
                        currentPassword.text.toString(),
                        newPasswordEdtxt.text.toString()
                    )
                }
                else{
                    showSnackbar("Current password doesn't match!")
                    currentPassword.text.clear()
                }
            }
        }
        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun changePassword(alertDialog: AlertDialog?, oldPassword: String, newPassword: String) {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null && firebaseUser.email != null) {
            val credential = EmailAuthProvider.getCredential(firebaseUser.email!!, oldPassword)
            firebaseUser.reauthenticate(credential)
                .addOnSuccessListener {
                    firebaseUser.updatePassword(newPassword)
                        .addOnSuccessListener {
                            alertDialog?.dismiss()
                            showSnackbar("Password changed!")
                            viewModel.currentUser.value?.data?.let{
                                updateUserDetails(it.name, it.email, newPassword, it.image, alertDialog)
                            }
                        }
                        .addOnFailureListener {
                            showSnackbar("Some error occured: $it")
                        }
                }
                .addOnFailureListener {
                    showSnackbar("Seems like something is wrong, try again in some time!")
                }
        }
    }

    private fun updateUserDetails(name: String, email: String, password: String, image: String, alertDialog: AlertDialog?) {
        GlobalScope.launch{
            firebase.collection("users").document(auth.currentUser!!.email!!).set(
                hashMapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password,
                    "image" to image
                )
            )
                .addOnSuccessListener {
                    updateCurrentUserInViewModel()
                    alertDialog?.dismiss()
                }
                .addOnFailureListener {
                    showSnackbar("Some error occurred: $it")
                }
        }
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
                    if(passwordEditText.text.toString() != viewModel.currentUser.value!!.data!!.password){
                        Toast.makeText(requireContext(), "Password Doesn't match!", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        changeName(nameEditText.text.toString(), alertDialog)
                    }
                }
            }
        }
        cancelBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    private fun changeName(newName: String, alertDialog: AlertDialog) {
        if(!viewModel.hasInternetConnection()){
            Toast.makeText(requireContext(), "Please connect to the internet", Toast.LENGTH_SHORT).show()
        }
        else {
            viewModel.currentUser.value?.data?.let{
                updateUserDetails(newName, it.email, it.password, it.image, alertDialog)
            }
        }
    }

    private fun updateCurrentUserInViewModel() {
        viewModel.currentUser.postValue(Resource.Loading())
        GlobalScope.launch{
             firebase.collection("users").document(auth.currentUser!!.email!!).get()
                 .addOnSuccessListener {
                     val newUserDetails = it.toObject(UserData::class.java)
                     newUserDetails.let{
                         if(newUserDetails==null) {
                             viewModel.currentUser.postValue(Resource.Error("couldn't fetch user details"))
                             Toast.makeText(requireContext(), "Couldn't update user data, please try again.", Toast.LENGTH_SHORT).show()
                         }
                         else {
                             viewModel.currentUser.postValue(Resource.Success(newUserDetails))
                             popupWindow?.dismiss()
                         }
                     }
                 }
                 .addOnFailureListener {
                     viewModel.currentUser.postValue(Resource.Error("couldn't fetch user details"))
                     Toast.makeText(requireContext(), "Couldn't update user data, please try again.", Toast.LENGTH_SHORT).show()
                 }
        }
    }

    private fun signOut() {
        auth.signOut()
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
