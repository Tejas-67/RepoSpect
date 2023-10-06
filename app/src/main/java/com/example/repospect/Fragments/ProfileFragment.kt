package com.example.repospect.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.repospect.Activities.LoginActivity
import com.example.repospect.Activities.MainActivity
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private lateinit var viewModel: RepoViewModel
    private var _binding: FragmentProfileBinding? = null
    private val binding get()=_binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore

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
        Glide.with(requireContext()).load(viewModel.currentUser.value?.data?.image).into(binding.userImage)

    }
}
