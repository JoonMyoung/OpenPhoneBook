package com.joonmyoung.openphonebook.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.joonmyoung.openphonebook.R
import com.joonmyoung.openphonebook.auth.IntroActivity
import com.joonmyoung.openphonebook.databinding.FragmentMySettingBinding
import com.joonmyoung.openphonebook.setting.MyProfileActivity


class MySettingFragment : Fragment(R.layout.fragment_my_setting) {

    private var binding: FragmentMySettingBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMySettingBinding = FragmentMySettingBinding.bind(view)
        binding = fragmentMySettingBinding

        binding!!.logoutBtn.setOnClickListener {
            //	로그아웃기능
            val auth = Firebase.auth
            auth.signOut()
            val intent = Intent(context, IntroActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding!!.myProfileSetting.setOnClickListener {
            val intent = Intent(context, MyProfileActivity::class.java)
            startActivity(intent)
        }




    }

}