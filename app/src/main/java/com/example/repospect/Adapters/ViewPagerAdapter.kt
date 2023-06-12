package com.example.repospect.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.repospect.Fragments.BranchFragment
import com.example.repospect.Fragments.IssueFragment
import com.example.repospect.UI.RepoViewModel
import com.example.repospect.listeners.ItemClickListener
import com.google.android.material.tabs.TabLayout

class ViewPagerAdapter(
    fragmentManger: FragmentManager,
    lifecycle: Lifecycle,
    viewModel: RepoViewModel,
    repoFullName: String,
    listener: ItemClickListener
) :
    FragmentStateAdapter(fragmentManger, lifecycle) {
    val _VM=viewModel
    val fullName=repoFullName
    val _listener=listener
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BranchFragment(_VM, fullName,_listener)
            1 -> IssueFragment(_VM, fullName)
            else -> Fragment()
        }
    }

}