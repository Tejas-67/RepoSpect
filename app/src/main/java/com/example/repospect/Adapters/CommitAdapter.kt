package com.example.repospect.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.repospect.DataModel.Commits
import com.example.repospect.DataModel.CommitsItem
import com.example.repospect.R
import com.example.repospect.listeners.ItemClickListener

class CommitAdapter(): RecyclerView.Adapter<CommitAdapter.CommitViewHolder>() {

    private var list: List<CommitsItem> = listOf()

    class CommitViewHolder(view: View): RecyclerView.ViewHolder(view){
        val message: TextView = view.findViewById(R.id.commit_message)
        val ownerName: TextView = view.findViewById(R.id.commit_by)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommitViewHolder {
        return CommitViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.commit_item_view, parent, false))
    }

    override fun getItemCount(): Int {
         return list.size
    }

    override fun onBindViewHolder(holder: CommitViewHolder, position: Int) {
        val item=list[position]
        holder.message.text=item.commit.message
        holder.ownerName.text=item.commit.committer.name
    }

     fun setList(l: List<CommitsItem>){
        list=l
        notifyDataSetChanged()
    }
}