package com.joonmyoung.openphonebook.Group.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.joonmyoung.openphonebook.R
import com.joonmyoung.openphonebook.auth.UserDataModel
import kotlin.coroutines.coroutineContext

class GroupMemberRVAdapter(private val memberList: MutableList<UserDataModel>,val context: Context):RecyclerView.Adapter<GroupMemberRVAdapter.ViewHolder>() {

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_group_members,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nickname = holder.itemView.findViewById<TextView>(R.id.groupMemberNicknameTextView)
        val comment = holder.itemView.findViewById<TextView>(R.id.groupMemberMessageTextView)

        nickname.text = memberList[position].nickname
        comment.text = memberList[position].comment

        getImage(position,holder)
    }

    override fun getItemCount(): Int {
       return memberList.size
    }

    private fun getImage(position: Int, holder: ViewHolder) {
        val imageArea = holder.itemView.findViewById<ImageView>(R.id.groupMemberProfileImage)
        // Reference to an image file in Cloud Storage
        val storageReference = Firebase.storage.reference.child(memberList[position].uid + ".png")

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {

                Glide.with(context)
                    .load(task.result)
                    .into(imageArea)

            } else {
//                Toast.makeText(this, "이미지 다운 실패", Toast.LENGTH_SHORT).show()


            }

        })
    }

}