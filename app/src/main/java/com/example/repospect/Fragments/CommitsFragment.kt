package com.example.repospect.Fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repospect.Activities.MainActivity
import com.example.repospect.Adapters.CommitAdapter
import com.example.repospect.DataModel.Repo
import com.example.repospect.DataModel.Resource
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentCommitsBinding

class CommitsFragment() : Fragment() {

    private var _binding: FragmentCommitsBinding? = null
    private val binding get()=_binding!!
    private lateinit var adapter: CommitAdapter
    private lateinit var branchName: String
    lateinit var viewModel: RepoViewModel
    lateinit var currentRepo: Repo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            val action=CommitsFragmentDirections.actionCommitsFragmentToViewRepoFragment(currentRepo)
            findNavController().navigate(action)
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        arguments?.let {
            branchName=it.getString("branchName")!!
            currentRepo=it.getParcelable("currentRepo")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel=(activity as MainActivity).viewModel
        _binding=FragmentCommitsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val temp=currentRepo.full_name!!.split('/')
        val owner=temp[0]
        val repoName=temp[1]
        setUpRecyclerView()
        if(viewModel.hasInternetConnection()) viewModel.getCommits(owner, repoName, branchName)
        else {
            showNoInternetPopup()
        }
        viewModel.currentRepoCommits.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success->{
                    adapter.setList(it.data!!)
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Couldn't fetch data",Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> Toast.makeText(requireContext(), "Loading.....",Toast.LENGTH_SHORT).show()
            }
        })
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
    private fun setUpRecyclerView(){
        adapter= CommitAdapter()
        binding.commitRcv.adapter=adapter
        binding.commitRcv.layoutManager=LinearLayoutManager(requireContext())
    }
}