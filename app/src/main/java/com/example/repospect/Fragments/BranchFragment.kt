package com.example.repospect.Fragments

import android.content.ClipData.Item
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repospect.Activities.MainActivity
import com.example.repospect.Adapters.BranchAdapter
import com.example.repospect.DataModel.Resource
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentBranchBinding
import com.example.repospect.listeners.ItemClickListener


class BranchFragment(val viewModel: RepoViewModel, val repoName: String, val listener: ItemClickListener) : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentBranchBinding? = null
    private val binding get()=_binding!!
    private lateinit var adapter: BranchAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentBranchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showProgressBar()
        setUpRecyclerView()
        val temp=repoName.split('/')
        viewModel.getAllBranches(temp[0], temp[1])
        viewModel.allBranches.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success->{
                    adapter.updateBranches(it.data!!)
                    hideProgressBar()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Error -> {
                    showToast("Error Fetching Branches")
                }
            }
        })
    }

    private fun showToast(){
        Toast.makeText(requireContext(), "PLease connext to the internt", Toast.LENGTH_SHORT).show()
    }

    private fun showProgressBar(){
        binding.progressBar.visibility=View.VISIBLE
        binding.branchFragmentLl.visibility=View.GONE
    }
    private fun hideProgressBar(){
        binding.progressBar.visibility=View.GONE
        binding.branchFragmentLl.visibility=View.VISIBLE
    }
    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun setUpRecyclerView(){
        adapter= BranchAdapter(listener)
        binding.branchRcv.adapter=adapter
        binding.branchRcv.layoutManager=LinearLayoutManager(requireContext())
    }
}