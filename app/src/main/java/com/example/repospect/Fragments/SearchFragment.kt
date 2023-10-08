package com.example.repospect.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
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
                    if(it.toString().isNotEmpty()) {
                        if(viewModel.hasInternetConnection()) viewModel.searchRepositories(it.toString())
                        else showNoInternetPopup()
                    }
                }
            }
        }
        viewModel.searchRepositoriesResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource){
                is Resource.Loading -> {
                    showAnim()
                }
                is Resource.Success -> {
                    hideAnim()
                    resource?.data?.items?.let{
                        adapter.updateRcv(it)
                    }
                }
                is Resource.Error -> {
                    hideAnim()
                    showToast("Search Failed: ${resource.message}")
                }
            }
        })
        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
        binding.searchTxt.setOnFocusChangeListener { _, hasFocus -> updateSearchBarFocus(hasFocus) }
    }

    private fun updateSearchBarFocus(hasFocus: Boolean) {
        if(hasFocus) binding.searchTxt.hint=""
        else binding.searchTxt.hint="/search"
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


    private fun hideAnim() {
        binding.loadingAnim.visibility=View.GONE
        binding.searchRcv.visibility=View.VISIBLE
    }
    private fun showAnim(){
        binding.loadingAnim.visibility=View.VISIBLE
        binding.searchRcv.visibility=View.GONE
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