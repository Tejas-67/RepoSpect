package com.example.repospect.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repospect.Activities.MainActivity
import com.example.repospect.Adapters.RepoAdapter
import com.example.repospect.DataModel.Repo
import com.example.repospect.DataModel.Resource
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentSearchBinding
import com.example.repospect.listeners.ItemClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SearchFragment : Fragment(), ItemClickListener {
    private lateinit var adapter: RepoAdapter
    private lateinit var listener: ItemClickListener
    private var _binding : FragmentSearchBinding? = null
    private val binding get()= _binding!!
    private lateinit var viewModel: RepoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        listener=this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as MainActivity).viewModel
        setUpRecyclerView()
        var job: Job? = null
        binding.searchTxt.addTextChangedListener {
            job?.cancel()
            job= GlobalScope.launch(Dispatchers.IO){
                it?.let{
                    if(it.toString().isNotEmpty()) viewModel.searchRepositories(it.toString())
                }
            }
        }
        viewModel.searchRepositoriesResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource){
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    resource?.data?.items?.let{
                        adapter.updateRcv(it)
                    }
                }
                is Resource.Error -> {
                    showToast("Search Failed: ${resource.message}")
                }
            }
        })
    }
    private fun showToast(message : String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun setUpRecyclerView(){
        adapter= RepoAdapter(listener)
        binding.searchRcv.adapter=adapter
        binding.searchRcv.layoutManager=LinearLayoutManager(requireContext())
    }

    override fun onRepoClicked(view: View, repo: Repo) {
        val action = SearchFragmentDirections.actionSearchFragmentToViewRepoFragment(repo)
        findNavController().navigate(action)
    }

    override fun onBranchSelected(view: View, branchName: String) {
        TODO("Not yet implemented")
    }

    override fun onDeleteButtonClicked(view: View, repo: Repo) {
        TODO("Not yet implemented")
    }
}