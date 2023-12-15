package com.example.repospect.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.repospect.DataModel.Repo
import com.example.repospect.R
import com.example.repospect.listeners.ItemClickListener

class RepoAdapter(val listener: ItemClickListener): RecyclerView.Adapter<RepoAdapter.SavedRepoViewHolder>() {

    private var list: List<Repo> = listOf()
    inner class SavedRepoViewHolder(view: View): RecyclerView.ViewHolder(view){
        val nameTv: TextView = view.findViewById(R.id.repo_name_tv)
        val languageTv: TextView = view.findViewById(R.id.languge_tv)
        val createdAt: TextView = view.findViewById(R.id.created_at_tv)
        val desc: TextView = view.findViewById(R.id.desc)
        val image: ImageView = view.findViewById(R.id.user_image_view)
        val deleteBtn: ImageButton =view.findViewById(R.id.btnDelete)
        val layout: CardView = view.findViewById(R.id.layout_main)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRepoViewHolder {
        return SavedRepoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.new_item_view, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SavedRepoViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(list[position].owner.avatar_url).into(holder.image)
        holder.nameTv.text=list[position].full_name
        holder.languageTv.text=list[position].language
        holder.createdAt.text="Updated on ${list[position].updated_at!!.split('T')[0]}"

        if(list[position].description.isNullOrEmpty()){
            holder.desc.visibility=View.GONE
        }
        else{
            holder.desc.visibility=View.VISIBLE
            holder.desc.text=list[position].description
        }
        holder.itemView.setOnLongClickListener {
            holder.layout.animate()
                .translationX(0f)
                .setDuration(200)
                .start()
            holder.deleteBtn.visibility=View.VISIBLE
            true
        }
        holder.itemView.setOnClickListener {
            if(holder.deleteBtn.visibility==View.GONE) listener.onRepoClicked(it, list[position])
            else {
                holder.deleteBtn.visibility = View.GONE
                holder.layout.translationX=0f
            }

        }
        holder.deleteBtn.setOnClickListener {
            listener.onDeleteButtonClicked(it, list[position])
        }
    }
    fun updateRcv(l: List<Repo>){
        list=l
        notifyDataSetChanged()

    }

}