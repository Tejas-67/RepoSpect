package com.example.repospect.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.repospect.Activities.MainActivity
import com.example.repospect.Adapters.ViewPagerAdapter
import com.example.repospect.DataModel.Repo
import com.example.repospect.DataModel.Resource
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentViewRepoBinding
import com.example.repospect.listeners.ItemClickListener
import com.google.android.material.tabs.TabLayoutMediator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ViewRepoFragment : Fragment(), ItemClickListener {

    private var _binding: FragmentViewRepoBinding?=null
    private val binding get()=_binding!!
    private lateinit var listener: ItemClickListener
    private lateinit var viewModel: RepoViewModel
    private var isSaved=false
    private lateinit var currentRepo: Repo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentRepo = it.getParcelable("currentRepo")!!
        }
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            moveToHomeFragment()
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listener=this
        viewModel=(activity as MainActivity).viewModel
        _binding= FragmentViewRepoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
        val adapter=ViewPagerAdapter(parentFragmentManager, lifecycle, viewModel, currentRepo.full_name!!, listener)
        binding.viewPager.adapter=adapter

        binding.saveBtn.setOnClickListener {
            viewModel.addNewRepoToLocal(currentRepo)
        }
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
        Glide.with(requireContext()).load(currentRepo.owner.avatar_url).into(binding.userImage)
        binding.languge.text=currentRepo.language
        binding.createdAt.text=currentRepo.created_at
        binding.toolbar.toolbarMainText.text="Repository Details"
        binding.repoName.text=currentRepo.full_name
        binding.descriptionRepo.text=currentRepo.description
        hideProgressBar()
    }
    private fun moveToHomeFragment(){
        viewModel.searchedRepo= MutableLiveData()
        val action=ViewRepoFragmentDirections.actionViewRepoFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onRepoClicked(view: View, repo: Repo) {
    }

    override fun onBranchSelected(view: View, branchName: String) {
        getCommitsAndNavigate(branchName)
    }

    private fun getCommitsAndNavigate(name: String){
        val action = ViewRepoFragmentDirections.actionViewRepoFragmentToCommitsFragment(name, currentRepo)
        findNavController().navigate(action)
    }

    override fun onDeleteButtonClicked(view: View, repo: Repo) {

    }

}