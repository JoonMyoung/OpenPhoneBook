package com.joonmyoung.openphonebook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.joonmyoung.openphonebook.fragment.MemoFragment
import com.joonmyoung.openphonebook.fragment.MyGroupFragment
import com.joonmyoung.openphonebook.fragment.MySettingFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myGroupFragment = MyGroupFragment()
        val groupSettingFragment = MemoFragment()
        val mySettingFragment = MySettingFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        replaceFragment(myGroupFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.myGroup -> replaceFragment(myGroupFragment)
                R.id.groupSetting -> replaceFragment(groupSettingFragment)
                R.id.mySetting -> replaceFragment(mySettingFragment)

            }
            true
        }

    }
    private fun replaceFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            commit()
        }

    }
}