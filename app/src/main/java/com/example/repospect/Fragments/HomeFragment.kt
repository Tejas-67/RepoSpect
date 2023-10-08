package com.example.repospect.Fragments

import android.animation.Animator
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.repospect.Activities.MainActivity
import com.example.repospect.Adapters.RepoAdapter
import com.example.repospect.DataModel.Repo
import com.example.repospect.DataModel.Resource
import com.example.repospect.DataModel.UserData
import com.example.repospect.R
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.databinding.FragmentHomeBinding
import com.example.repospect.listeners.ItemClickListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), ItemClickListener {

    private var _binding : FragmentHomeBinding? =null
    private val binding get()=_binding!!
    private lateinit var adapter: RepoAdapter
    private lateinit var listener: ItemClickListener
    private lateinit var auth: FirebaseAuth
    private lateinit var firebase: FirebaseFirestore
    private lateinit var viewModel: RepoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel=(activity as MainActivity).viewModel
        if(!viewModel.hasInternetConnection()) {
            showNoInternetPopup()
        }
        else{
            auth = FirebaseAuth.getInstance()
            firebase = FirebaseFirestore.getInstance()
            viewModel.currentUser.value.let{
                if(it==null){
                    loadUserDetails()
                }
                else{
                    when(it){
                        is Resource.Success->{}
                        is Resource.Error->{
                            showSnackbar("Some error occurred while fetching user details")
                        }
                        else->{}
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listener=this
        _binding= FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        binding.toolbar.search.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
        viewModel.currentUser.observe(viewLifecycleOwner, Observer{
            when(it){
                is Resource.Success -> updateUI(it.data!!)
                is Resource.Error -> showSnackbar(it.message!!)
                else -> {}
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener {
            if(!viewModel.hasInternetConnection()) showNoInternetPopup()
            else{
                binding.swipeRefreshLayout.isRefreshing=false
                fetchData()
            }
        }
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
        binding.toolbar.profilePic.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun loadUserDetails() {
        showSnackbar("Loading User Data")
        GlobalScope.launch{
            viewModel.currentUser.postValue(Resource.Loading())
            firebase.collection("users").document(auth.currentUser!!.email!!).get()
                .addOnSuccessListener {
                    Log.w("AndroidRuntime", it.toString())
                    val userObject = it.toObject(UserData::class.java)
                    if(userObject==null) {
                        viewModel.currentUser.postValue(Resource.Error("Error fetching user details!"))
                    }
                    else{
                        viewModel.currentUser.postValue(Resource.Success(userObject))
                    }
                }
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

    private fun updateUI(user: UserData) {
            Glide.with(requireContext()).load(user.image).into(binding.toolbar.profilePic)
    }

    private fun fetchData() {
        showRefreshAnimation()
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.startLocalDataUpdate()
            viewModel.ifDataUpdated.observe(viewLifecycleOwner, Observer{
                if(it){
                    hideRefreshAnimation()
                }
            })
        }, 1500)
        viewModel.ifDataUpdated.postValue(false)
    }

    private fun hideRefreshAnimation() {
        binding.refreshAnimation.visibility=View.GONE
        binding.savedRepoRcv.visibility=View.VISIBLE
    }

    private fun showRefreshAnimation() {
        binding.savedRepoRcv.visibility=View.GONE
        binding.refreshAnimation.visibility=View.VISIBLE
        binding.refreshAnimation.playAnimation()
    }

    private fun showAnimation(){
        binding.apply {
            savedRepoRcv.visibility=View.GONE
            swipeRefreshLayout.visibility=View.GONE
            emptyListAnimation.visibility=View.VISIBLE
            clickOnButton.visibility=View.VISIBLE
            noRepo.visibility=View.VISIBLE
            emptyListAnimation.playAnimation()
        }
    }
    private fun hideAnimation(){
        binding.apply {
            swipeRefreshLayout.visibility=View.VISIBLE
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
        adapter=RepoAdapter(listener)
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
    private fun showSnackbar(message: String){
        activity?.let { Snackbar.make(requireContext(), it.findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show() }
    }

    override fun onBranchSelected(view: View, branchName: String) {
        //Not Required!
    }
    private fun showToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteButtonClicked(view: View, repo: Repo) {
        viewModel.deleteRepoFromLocal(repo)
        //animate view while deleting.
    }
}