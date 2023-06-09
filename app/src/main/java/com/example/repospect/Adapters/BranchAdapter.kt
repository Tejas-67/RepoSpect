package com.example.repospect.Adapters

import android.content.ClipData.Item
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.repospect.DataModel.Branch
import com.example.repospect.R
import com.example.repospect.listeners.ItemClickListener

class BranchAdapter(val listener: ItemClickListener): RecyclerView.Adapter<BranchAdapter.BranchViewHolder>() {

    private var list: ArrayList<Branch> = arrayListOf()

    class BranchViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val branchName: TextView = view.findViewById(R.id.repo_name_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder {
        return BranchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.branch_item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
        holder.branchName.text=list[position].name
        holder.itemView.setOnClickListener { listener.onBranchSelected(it, list[position].name) }
    }

    fun updateBranches(l: ArrayList<Branch>){
        list=l
        notifyDataSetChanged()
    }
}