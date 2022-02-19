package com.joonmyoung.openphonebook.auth

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.joonmyoung.openphonebook.MainActivity
import com.joonmyoung.openphonebook.R
import com.joonmyoung.openphonebook.base.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val uid = mAuth.currentUser?.uid.toString()
        Log.d("TAG",uid)


        if (uid =="null"){
            Handler().postDelayed({
                val intent = Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            },3000)

        }else{
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(intent)
                finish()
            },1000)
        }



    }

    override fun setValues() {

    }

    override fun setUpEvents() {

    }
}