package com.example.repospect.Fragments

import android.animation.Animator
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.repospect.Activities.MainActivity
import com.example.repospect.DataModel.Repo
import com.example.repospect.DataModel.Resource
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentAddRepoBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class AddRepoFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    private var _binding : FragmentAddRepoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel:RepoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            clearFocus()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
    private fun clearFocus() {
        val focusedView = view?.findFocus()
        if (focusedView != null) {
            focusedView.clearFocus()
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        }
        else{
            navigateToHome()
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
        hideProgressBar()
        binding.getRepoBtn.setOnClickListener {
            if(binding.repoNameEditText.text.isNullOrEmpty() || binding.ownerNameEditText.text.isNullOrEmpty()){
                showToast("Please enter Complete Details")
            }
            else{
                if(viewModel.hasInternetConnection()) viewModel.searchRepoWithOwnerAndName(binding.ownerNameEditText.text.toString(), binding.repoNameEditText.text.toString())
                else showNoInternetPopup()
            }

        }

        viewModel.searchedRepo.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    if(!it.data!!.full_name.isNullOrEmpty()) navigateToViewRepoFragment(it.data!!)
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Error -> {
                    showToast("No Such Repo Found")
                    navigateToHome()
                }
            }
        })
        binding.toolbar.setOnClickListener {
            findNavController().navigate(R.id.action_addRepoFragment_to_homeFragment)
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

    private fun navigateToHome(){
        viewModel.searchedRepo= MutableLiveData()
        val action=AddRepoFragmentDirections.actionAddRepoFragmentToHomeFragment()
        findNavController().navigate(action)
    }
    private fun showProgressBar(){
        binding.progressBar.visibility=View.VISIBLE
        binding.addRepoLl.visibility=View.GONE
    }
    private fun hideProgressBar(){
        binding.progressBar.visibility=View.GONE
        binding.addRepoLl.visibility=View.VISIBLE
    }
    private fun navigateToViewRepoFragment(repo: Repo){
        val action=AddRepoFragmentDirections.actionAddRepoFragmentToViewRepoFragment(repo)
        findNavController().navigate(action)
    }
    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}