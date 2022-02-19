package com.joonmyoung.openphonebook.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.joonmyoung.openphonebook.Group.GroupDataModel
import com.joonmyoung.openphonebook.Group.GroupInsideActivity
import com.joonmyoung.openphonebook.Group.GroupRVAdapter
import com.joonmyoung.openphonebook.R
import com.joonmyoung.openphonebook.databinding.FragmentMyGroupBinding
import com.joonmyoung.openphonebook.utils.FirebaseAuthUtils
import com.joonmyoung.openphonebook.utils.FirebaseRef


class MyGroupFragment : Fragment(R.layout.fragment_my_group) {

    private var binding : FragmentMyGroupBinding? = null
    private val uid = FirebaseAuthUtils.getUid()
    private val groupMember = mutableListOf<String>()
    private val groupDataModelList = mutableListOf<GroupDataModel>()
    private lateinit var groupRVAdapter: GroupRVAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMyGroupBinding = FragmentMyGroupBinding.bind(view)
        binding = fragmentMyGroupBinding

        initGroupRecyclerView()
        getMyGroup()



        fragmentMyGroupBinding.groupAddBtn.setOnClickListener {

          showDialog()

        }



    }

    private fun showDialog() {
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_group_create, null)
        val mBuilder = AlertDialog.Builder(requireContext()).setView(mDialogView)

        val alertDialog = mBuilder.show()

        alertDialog.findViewById<Button>(R.id.groupCreateBtn)?.setOnClickListener {

            val edit = alertDialog.findViewById<TextInputEditText>(R.id.roomNameEdit)
            if (edit?.text!!.isEmpty()){
                Toast.makeText(context, "방이름을 설정해주세요", Toast.LENGTH_SHORT).show()
            }else{
                try {
                    groupMember.clear()
                }catch (e:Exception){

                }
                val groupName = edit.text.toString()
                val groupKey = uid + System.currentTimeMillis()
                groupMember.add(uid)
                val groupDataModel =
                    GroupDataModel(groupKey ,uid, groupName,groupMember)

                // 데이터베이스에 쓰기
                FirebaseRef.groupInfoRef.child(groupKey).setValue(groupDataModel)
                Toast.makeText(context, "생성 되었습니다.", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()

            }



        }
        alertDialog.findViewById<Button>(R.id.groupCancelBtn)?.setOnClickListener {
            alertDialog.dismiss()
        }


    }
    private fun getMyGroup() {


        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                groupDataModelList.clear()

                Log.d("TAG", dataSnapshot.toString())
                if (dataSnapshot.children.count()==0){
                    Toast.makeText(context, "데이터 없음", Toast.LENGTH_SHORT).show()

                }else{
                    for (dataModel in dataSnapshot.children){

                        Log.d("TAG:dataModel",dataModel.toString())
                        val item = dataModel.getValue(GroupDataModel::class.java)
                        Log.d("TAG:item",item.toString())

                        for (i in item!!.groupMember){
                            Log.d("TAG",i)
                            if (i == uid){
                                groupDataModelList.add(item)
                            }
                        }
                        groupRVAdapter.notifyDataSetChanged()



                    }

                }

//                val data = dataSnapshot.getValue(GroupDataModel::class.java)
//                Log.d("TAG",data.toString())


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())

            }
        }
        FirebaseRef.groupInfoRef.addValueEventListener(postListener)
    }

    private fun initGroupRecyclerView(){

        groupRVAdapter = GroupRVAdapter(
            groupDataModelList,requireContext(), onItemClicked = {startGroupInside(it)}
        )
        binding?.groupListRecyclerView?.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        binding?.groupListRecyclerView?.adapter = groupRVAdapter

    }

    private fun startGroupInside(groupDataModel: GroupDataModel){
        context.let {
            val intent = Intent(it,GroupInsideActivity::class.java)
            intent.putExtra("GroupKey",groupDataModel.groupKey)
            startActivity(intent)
        }



    }






}