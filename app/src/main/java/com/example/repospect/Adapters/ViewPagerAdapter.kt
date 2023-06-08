package com.example.repospect.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.repospect.Fragments.BranchFragment
import com.example.repospect.Fragments.IssueFragment
import com.google.android.material.tabs.TabLayout

class ViewPagerAdapter(
    fragmentManger: FragmentManager,
    lifecycle: Lifecycle
) :
    FragmentStateAdapter(fragmentManger, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BranchFragment()
            1 -> IssueFragment()
            else -> Fragment()
        }
    }

}