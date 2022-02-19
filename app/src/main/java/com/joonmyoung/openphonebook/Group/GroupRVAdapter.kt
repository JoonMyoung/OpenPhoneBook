package com.joonmyoung.openphonebook.Group

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joonmyoung.openphonebook.R
import com.joonmyoung.openphonebook.utils.FirebaseAuthUtils

class GroupRVAdapter(private val groupList:MutableList<GroupDataModel>,val context : Context,val onItemClicked:(GroupDataModel)->Unit)
    :RecyclerView.Adapter<GroupRVAdapter.ViewHolder>(){
    private val uid = FirebaseAuthUtils.getUid()

    class ViewHolder(view:View):RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_group,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val title = holder.itemView.findViewById<TextView>(R.id.groupTitleTextView)
        val groupMemberCount = holder.itemView.findViewById<TextView>(R.id.groupMemberCountTextView)

        title.text = groupList[position].groupName
        groupMemberCount.text = groupList[position].groupMember.size.toString()+"명"

        if (groupList[position].creatorUid==uid){
            val creatorImage = holder.itemView.findViewById<ImageView>(R.id.GroupCreatorImage)
            creatorImage.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener {
            onItemClicked(groupList[position])
        }



    }

    override fun getItemCount(): Int {
        return groupList.size
    }
}