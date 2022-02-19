package com.joonmyoung.openphonebook.Group.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.joonmyoung.openphonebook.Group.Adapter.GroupMemberRVAdapter
import com.joonmyoung.openphonebook.Group.GroupDataModel
import com.joonmyoung.openphonebook.Group.GroupRVAdapter
import com.joonmyoung.openphonebook.R
import com.joonmyoung.openphonebook.auth.UserDataModel
import com.joonmyoung.openphonebook.databinding.FragmentGroupMembersBinding
import com.joonmyoung.openphonebook.utils.FirebaseRef

class GroupMembersFragment : Fragment(R.layout.fragment_group_members) {

    private var binding : FragmentGroupMembersBinding? = null
    private var groupKey = ""
    private var memberList = mutableListOf<String>()
    private var memberInfoList = mutableListOf<UserDataModel>()
    private lateinit var groupMemberRVAdapter: GroupMemberRVAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_members, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentGroupMembersBinding = FragmentGroupMembersBinding.bind(view)
        binding = fragmentGroupMembersBinding

        groupKey = arguments?.getString("groupKey").toString()
        Log.d("TAG:groupKey",groupKey)

        getGroupInfo()
        initGroupMemberRecyclerView()



    }

    private fun getGroupInfo(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                memberList.clear()
                Log.d("TAG", dataSnapshot.toString())
                if (dataSnapshot.children.count()==0){
                    Toast.makeText(context, "띠용", Toast.LENGTH_SHORT).show()
                }else{
                    val item = dataSnapshot.getValue(GroupDataModel::class.java)
                    Log.d("TAG:item",item.toString())
                    memberList = item!!.groupMember
                    Log.d("TAG:memberList",memberList.toString())
                }

                getUserInfo()


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())

            }
        }
        FirebaseRef.groupInfoRef.child(groupKey).addValueEventListener(postListener)
    }

    private fun getUserInfo(){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                memberInfoList.clear()
                Log.d("TAG:dataSnapShot",dataSnapshot.toString())
                for (dataModel in dataSnapshot.children){
                    Log.d("TAG:dataModel",dataModel.toString())
                    val user = dataModel.getValue(UserDataModel::class.java)

                    for (i in memberList) {
                        if (i == user!!.uid.toString()){
                            memberInfoList.add(user)
                        }
                    }
                }
                groupMemberRVAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())

            }
        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)

    }

    private fun initGroupMemberRecyclerView(){

        groupMemberRVAdapter = GroupMemberRVAdapter(memberInfoList,requireContext())

        binding?.groupMemberRecyclerView?.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,false)
        binding?.groupMemberRecyclerView?.adapter = groupMemberRVAdapter



    }


}