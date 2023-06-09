package com.example.repospect.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repospect.Adapters.IssueAdapter
import com.example.repospect.DataModel.Resource
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentIssueBinding


class IssueFragment(val viewModel: RepoViewModel, val repoName: String) : Fragment() {
    private var _binding: FragmentIssueBinding? = null
    private val binding get()=_binding!!
    private lateinit var adapter: IssueAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentIssueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showProgressBar()
        setUpRecyclerView()

        val temp=repoName.split('/')
        viewModel.getIssues(temp[0],temp[1])
        viewModel.currentRepoIssues.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Success -> {
                    adapter.setData(it.data!!)
                    hideProgressBar()
                }
                is Resource.Error -> {
                    hideProgressBar()
                    showToast("Error Fetching Issues")
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    private fun setUpRecyclerView(){
        adapter= IssueAdapter()
        binding.issueRcv.adapter=adapter
        binding.issueRcv.layoutManager=LinearLayoutManager(requireContext())
    }

    private fun showProgressBar(){
        binding.progressBar.visibility=View.VISIBLE
        binding.issueFragmentLl.visibility=View.GONE
    }

    private fun hideProgressBar(){
        binding.progressBar.visibility=View.GONE
        binding.issueFragmentLl.visibility=View.VISIBLE
    }

}