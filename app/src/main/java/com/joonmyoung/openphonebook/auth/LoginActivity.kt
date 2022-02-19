package com.joonmyoung.openphonebook.auth


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.joonmyoung.openphonebook.MainActivity
import com.joonmyoung.openphonebook.base.BaseActivity
import com.joonmyoung.openphonebook.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setValues()
        setUpEvents()

        binding.loginBtn.setOnClickListener{

            val emailCheck = binding.emailArea.text.toString()
            val passwordCheck = binding.passwordArea.text.toString()

            when {
                emailCheck.isEmpty() -> {
                    showSnackBar("이메일을 입력해주세요.")
                }
                passwordCheck.isEmpty() -> {
                    showSnackBar("비밀번호를 입력해주세요.")
                }
                else -> {

                    mAuth.signInWithEmailAndPassword(binding.emailArea.text.toString(), binding.passwordArea.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)

                            } else {

                                showSnackBar("로그인 실패 이메일과 비밀번호를 확인해주세요")

                            }
                        }
                }
            }


        }


    }

    override fun setValues() {

    }

    override fun setUpEvents() = with(binding){

    }

    private fun showSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }

}