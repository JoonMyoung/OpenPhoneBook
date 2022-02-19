package com.joonmyoung.openphonebook.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joonmyoung.openphonebook.R
import com.joonmyoung.openphonebook.databinding.FragmentMemoBinding


class MemoFragment : Fragment(R.layout.fragment_memo) {

    private var binding : FragmentMemoBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_memo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentGroupSettingBinding = FragmentMemoBinding.bind(view)
        binding = fragmentGroupSettingBinding

    }


}