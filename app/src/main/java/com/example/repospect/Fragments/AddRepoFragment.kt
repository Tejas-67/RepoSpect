package com.example.repospect.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.repospect.Activities.MainActivity
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentAddRepoBinding


class AddRepoFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding : FragmentAddRepoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel:RepoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel=(activity as MainActivity).viewModel
        _binding= FragmentAddRepoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.toolbarMainText.text="Add New Repo"

        binding.getRepoBtn.setOnClickListener {
            if(binding.repoNameEditText.text.isNullOrEmpty() || binding.ownerNameEditText.text.isNullOrEmpty()){
                showToast("Please enter Complete Details")
            }
            else{
                viewModel.searchRepoWithOwnerAndName(binding.ownerNameEditText.text.toString(),
                binding.repoNameEditText.text.toString())
                navigateToViewRepoFragment()
            }
        }
    }
    private fun navigateToViewRepoFragment(){
        val action=AddRepoFragmentDirections.actionAddRepoFragmentToViewRepoFragment()
        findNavController().navigate(action)
    }
    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}