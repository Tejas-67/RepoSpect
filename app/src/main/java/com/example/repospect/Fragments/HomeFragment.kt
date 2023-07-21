package com.example.repospect.Fragments

import android.animation.Animator
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
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentHomeBinding
import com.example.repospect.listeners.ItemClickListener

class HomeFragment : Fragment(), ItemClickListener {

    private var _binding : FragmentHomeBinding? =null
    private val binding get()=_binding!!
    private lateinit var adapter: SavedRepoAdapter
    private lateinit var listener: ItemClickListener

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
        listener=this
        viewModel=(activity as MainActivity).viewModel
        _binding= FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        viewModel.allLocalRepo.observe(viewLifecycleOwner, Observer {
            if(it.size==0){
                showAnimation()
            }
            else{
                hideAnimation()
                updateRcv(it)
            }
        })
        binding.addRepoBtn.setOnClickListener {
            navigateToAddRepoFragment()
        }
    }

    private fun showAnimation(){
        binding.apply {
            savedRepoRcv.visibility=View.GONE
            emptyListAnimation.visibility=View.VISIBLE
            clickOnButton.visibility=View.VISIBLE
            noRepo.visibility=View.VISIBLE
            emptyListAnimation.playAnimation()
            emptyListAnimation.addAnimatorListener(object: Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    emptyListAnimation.playAnimation()
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }

            })
        }
    }
    private fun hideAnimation(){
        binding.apply {
            savedRepoRcv.visibility=View.VISIBLE
            emptyListAnimation.visibility=View.GONE
            noRepo.visibility=View.GONE
            clickOnButton.visibility=View.GONE
        }
    }
    private fun navigateToAddRepoFragment(){
        val action = HomeFragmentDirections.actionHomeFragmentToAddRepoFragment()
        findNavController().navigate(action)
    }

    private fun setUpRecyclerView(){
        adapter=SavedRepoAdapter(listener)
        binding.savedRepoRcv.adapter=adapter
        binding.savedRepoRcv.layoutManager=LinearLayoutManager(requireContext())
    }

    private fun updateRcv(list: List<Repo>){
        adapter.updateRcv(list)
    }

    override fun onRepoClicked(view: View, repo: Repo) {
        val action = HomeFragmentDirections.actionHomeFragmentToViewRepoFragment(repo)
        findNavController().navigate(action)
    }

    override fun onBranchSelected(view: View, branchName: String) {
        //Not Required!
    }

    override fun onDeleteButtonClicked(view: View, repo: Repo) {
        viewModel.deleteRepoFromLocal(repo)
        //animate view while deleting.
    }
}