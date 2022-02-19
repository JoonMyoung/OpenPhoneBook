package com.joonmyoung.openphonebook.base

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.joonmyoung.openphonebook.R

abstract class BaseActivity : AppCompatActivity() {

    lateinit var mContext: Context
    lateinit var mAuth: FirebaseAuth
    lateinit var mViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        mContext = this
        mAuth = FirebaseAuth.getInstance()
    }

    abstract fun setValues()
    abstract fun setUpEvents()

    fun hideKeyboard() {
        if (this.currentFocus != null) {

            val inputManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                this.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}