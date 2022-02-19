package com.joonmyoung.openphonebook.Group.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joonmyoung.openphonebook.R
import com.joonmyoung.openphonebook.databinding.FragmentGroupNoticeBinding


class GroupNoticeFragment : Fragment(R.layout.fragment_group_notice) {

    private var binding : FragmentGroupNoticeBinding? = null
    private var groupKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_notice, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentGroupNoticeBinding = FragmentGroupNoticeBinding.bind(view)
        binding = fragmentGroupNoticeBinding

        groupKey = arguments?.getString("groupKey").toString()
        Log.d("TAG:groupKey",groupKey)
    }

}