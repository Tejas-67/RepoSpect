package com.example.repospect.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
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
import com.google.android.material.button.MaterialButton
import com.google.android.play.core.integrity.e
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var viewModel: RepoViewModel
    private var _binding: FragmentProfileBinding? = null
    private val binding get()=_binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private var popupWindow: PopupWindow? = null

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
                updateUserDetails()

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
            firebase.collection("users").document(auth.currentUser!!.email!!).set(
                hashMapOf(
                    "name" to newName,
                    "email" to viewModel.currentUser.value!!.data!!.email,
                    "password" to viewModel.currentUser.value!!.data!!.password,
                    "image" to viewModel.currentUser.value?.data?.image
                )
            ).addOnSuccessListener {
                alertDialog.dismiss()
                Toast.makeText(requireContext(), "Name updated", Toast.LENGTH_SHORT).show()
                updateCurrentUserInViewModel()
            }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error occured: $it", Toast.LENGTH_SHORT)
                        .show()
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

    private fun updateUserDetails() {
        Glide.with(requireContext())
            .load(viewModel.currentUser.value?.data?.image)
            .into(binding.userImage)
    }
}
