package com.example.repospect.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repospect.DataModel.Repo
import com.example.repospect.R

class SavedRepoAdapter: RecyclerView.Adapter<SavedRepoAdapter.SavedRepoViewHolder>() {

    private var list: List<Repo> = listOf()
    class SavedRepoViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val nameTv: TextView = view.findViewById(R.id.repo_name_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRepoViewHolder {
        return SavedRepoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SavedRepoViewHolder, position: Int) {
        holder.nameTv.text=list[position].full_name
    }

    fun updateRcv(l: List<Repo>){
        list=l
        notifyDataSetChanged()
    }
}