package com.joonmyoung.openphonebook.Group

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.joonmyoung.openphonebook.Group.fragment.GroupMembersFragment
import com.joonmyoung.openphonebook.Group.fragment.GroupNoticeFragment
import com.joonmyoung.openphonebook.Group.fragment.GroupRecordFragment
import com.joonmyoung.openphonebook.R
import com.joonmyoung.openphonebook.databinding.ActivityGroupInsideBinding
import com.joonmyoung.openphonebook.fragment.MemoFragment
import com.joonmyoung.openphonebook.fragment.MyGroupFragment
import com.joonmyoung.openphonebook.fragment.MySettingFragment
import com.joonmyoung.openphonebook.utils.FirebaseRef

class GroupInsideActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityGroupInsideBinding.inflate(layoutInflater)
    }
    private var groupKey : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        groupKey = intent.getStringExtra("GroupKey").toString()

        getGroupInfo()

        setSupportActionBar(binding.groupToolbar)

        val groupMembersFragment = GroupMembersFragment()
        val groupRecordFragment = GroupRecordFragment()
        val groupNoticeFragment = GroupNoticeFragment()

        val groupNavigationView = binding.groupNavigationView

        var bundle = Bundle()
        bundle.putString("groupKey",groupKey)

        groupMembersFragment.arguments = bundle
        groupRecordFragment.arguments = bundle
        groupNoticeFragment.arguments = bundle

        replaceFragment(groupMembersFragment)

        groupNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.myGroup -> replaceFragment(groupMembersFragment)
                R.id.groupSetting -> replaceFragment(groupRecordFragment)
                R.id.mySetting -> replaceFragment(groupNoticeFragment)

            }
            true
        }






    }

    private fun getGroupInfo(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.children.count()==0){
                    Toast.makeText(this@GroupInsideActivity, "??????", Toast.LENGTH_SHORT).show()
                }else{
                    val item = dataSnapshot.getValue(GroupDataModel::class.java)

                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    supportActionBar?.setTitle(item?.groupName)

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())

            }
        }
        FirebaseRef.groupInfoRef.child(groupKey).addValueEventListener(postListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                finish()
            }
            R.id.group_member_add_btn ->{
                Toast.makeText(this, "????????? ??????????????????", Toast.LENGTH_SHORT).show()
                Log.d("TAG","????????? ??????????????????")
                showDialog()
            }
            R.id.memo_add_btn->{
                Toast.makeText(this, "?????? ??????????????????", Toast.LENGTH_SHORT).show()
                Log.d("TAG","?????? ??????????????????")
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.group_member_item,menu)
        return true
    }

    private fun replaceFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.groupFragmentContainer, fragment)
            commit()
        }

    }
    private fun showDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_group_member_add, null)
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)

        val alertDialog = mBuilder.show()

        alertDialog.findViewById<Button>(R.id.groupMemberAddBtn)?.setOnClickListener {

            val edit = alertDialog.findViewById<TextInputEditText>(R.id.groupMemberNameEdit)
            if (edit?.text!!.isEmpty()){
               showSnackBar("????????? ??????????????????")
            }else{

                // ????????????????????? ??????
//                FirebaseRef.groupInfoRef.child(groupKey).setValue(groupDataModel)

                alertDialog.dismiss()

            }



        }
        alertDialog.findViewById<Button>(R.id.groupMemberAddCancelBtn)?.setOnClickListener {
            alertDialog.dismiss()
        }


    }

    private fun showSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }

}