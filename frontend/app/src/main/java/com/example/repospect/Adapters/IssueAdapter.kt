package com.example.repospect.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.repospect.DataModel.Issue
import com.example.repospect.R

class IssueAdapter : RecyclerView.Adapter<IssueAdapter.IssueViewHolder>() {

    private var list: List<Issue> = listOf()

    class IssueViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val titleTv: TextView = view.findViewById(R.id.issue_title)
        val issueNumber: TextView = view.findViewById(R.id.issue_number_created_at)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        return IssueViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.issue_item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        val item=list[position]
        holder.issueNumber.text="#${item.number} created at ${item.created_at.split('T')[0]}."
        holder.titleTv.text=item.title
    }

    fun setData(l: List<Issue>){
        list=l
        notifyDataSetChanged()
    }
}