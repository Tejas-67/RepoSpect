package com.example.repospect.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.repospect.Activities.MainActivity
import com.example.repospect.Adapters.SavedRepoAdapter
import com.example.repospect.DataModel.Repo
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? =null
    private val binding get()=_binding!!
    private lateinit var adapter: SavedRepoAdapter

    private lateinit var viewModel: RepoViewModel
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
        _binding= FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        viewModel.allLocalRepo.observe(viewLifecycleOwner, Observer {
            updateRcv(it)
        })
        binding.addRepoBtn.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddRepoFragment()
            findNavController().navigate(action)
        }
    }

    private fun setUpRecyclerView(){
        adapter=SavedRepoAdapter()
        binding.savedRepoRcv.adapter=adapter
        binding.savedRepoRcv.layoutManager=LinearLayoutManager(requireContext())
    }

    private fun updateRcv(list: List<Repo>){
        adapter.updateRcv(list)
    }
}