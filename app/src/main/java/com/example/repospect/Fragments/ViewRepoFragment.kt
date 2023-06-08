package com.example.repospect.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.repospect.Activities.MainActivity
import com.example.repospect.Adapters.ViewPagerAdapter
import com.example.repospect.DataModel.Repo
import com.example.repospect.DataModel.Resource
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentViewRepoBinding
import com.google.android.material.tabs.TabLayoutMediator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ViewRepoFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentViewRepoBinding?=null
    private val binding get()=_binding!!

    private lateinit var viewModel: RepoViewModel
    private var isSaved=false
    private lateinit var currentRepo: Repo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel=(activity as MainActivity).viewModel
        _binding= FragmentViewRepoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showProgressBar()
        binding.toolbar.toolbarMainText.text="Repository Details"
        val adapter=ViewPagerAdapter(parentFragmentManager, lifecycle, viewModel)
        binding.viewPager.adapter=adapter

        binding.saveBtn.setOnClickListener {
            if(isSaved) showToast("Already Saved!")
            else {
                isSaved=true
                viewModel.addNewRepoToLocal(currentRepo)
            }
        }
        viewModel.searchedRepo.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    currentRepo=it.data!!
                    updateUI()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Couldn't find any such repository!", Toast.LENGTH_SHORT).show()
                    moveToHomeFragment()
                }
                is Resource.Loading -> showProgressBar()

            }
        })

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            when (pos) {
                0 -> tab.text = "Branches"
                1 -> tab.text = "Issues"
            }
        }.attach()

    }
    private fun showProgressBar(){
        binding.progressBar.visibility=View.VISIBLE
        binding.viewRepoFragmentLl.visibility=View.GONE
    }
    private fun hideProgressBar(){
        binding.progressBar.visibility=View.GONE
        binding.viewRepoFragmentLl.visibility=View.VISIBLE
    }

    private fun updateUI(){
        hideProgressBar()
        binding.repoName.text=currentRepo.full_name
        binding.descriptionRepo.text=currentRepo.description
    }
    private fun moveToHomeFragment(){
        val action=ViewRepoFragmentDirections.actionViewRepoFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}