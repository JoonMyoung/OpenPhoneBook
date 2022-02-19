package com.joonmyoung.openphonebook.auth

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import com.joonmyoung.openphonebook.MainActivity
import com.joonmyoung.openphonebook.R
import com.joonmyoung.openphonebook.base.BaseActivity
import com.joonmyoung.openphonebook.base.MainViewModel
import com.joonmyoung.openphonebook.databinding.ActivityJoinBinding
import com.joonmyoung.openphonebook.utils.FirebaseRef
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.TimeUnit

class JoinActivity : BaseActivity() {

    private val binding by lazy {
        ActivityJoinBinding.inflate(layoutInflater)
    }

    private lateinit var verificationId: String

    private var uid = ""
    private var email = ""
    private var phoneNumber = ""
    private var nickname = ""
    private var comment = ""




    private val callbacks by lazy {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(p0: FirebaseException) {
                showSnackBar("인증실패")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)

                this@JoinActivity.verificationId = verificationId
                showSnackBar("인증코드 전송완료, 60초 안에 입력 해 주세요")

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setValues()
        setUpEvents()

        mViewModel.currentCertify.observe(this){
            checkSignUpReady()
        }

        binding.joinBtn.setOnClickListener {
            val emailCheck = binding.emailArea.text.toString()
            val passwordCheck = binding.passwordArea.text.toString()
            val passwordCheckBox = binding.passwordCheckArea.text.toString()
            val nicknameCheck = binding.nicknameArea.text.toString()

            when {
                emailCheck.isEmpty() -> {
                    showSnackBar("이메일을 입력 해 주세요.")
                }
                passwordCheck.isEmpty() -> {
                    showSnackBar("비밀번호를 입력 해 주세요.")
                }
                passwordCheckBox.isEmpty() -> {
                    showSnackBar("비밀번호 확인을 입력 해 주세요.")
                }
                nicknameCheck.isEmpty() -> {
                    showSnackBar("닉네임을 입력해주세요.")
                }
                passwordCheck != passwordCheckBox -> {
                    showSnackBar("비밀번호를 똑같이 입력해주세요.")
                }

                else -> {

                    email = emailCheck
                    phoneNumber = mViewModel.getPhoneNum()
                    nickname = nicknameCheck


                    mAuth.createUserWithEmailAndPassword(emailCheck,passwordCheck)
                        .addOnCompleteListener(this){ task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "createUserWithEmail:success")

                                // 유저 고유아이디 가져오기
                                val user = mAuth.currentUser
                                uid = user?.uid.toString()
                                // Token 받아오기

                                FirebaseMessaging.getInstance().token.addOnCompleteListener(

                                    OnCompleteListener { task ->
                                        if (!task.isSuccessful) {
                                            Log.w(
                                                "TAG",
                                                "Fetching FCM registration token failed",
                                                task.exception
                                            )
                                            return@OnCompleteListener
                                        }

                                        // Get new FCM registration token
                                        val token = task.result

                                        // Log and toast
                                        Log.d("TAG", token.toString())

                                        val userModel =
                                            UserDataModel(uid, email, phoneNumber, nickname,comment, token)

                                        // 데이터베이스에 쓰기
                                        FirebaseRef.userInfoRef.child(uid).setValue(userModel)
                                        uploadImage(uid)


                                        // 메인으로 이동
                                        val intent = Intent(this, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)

                                    })


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "createUserWithEmail:failure", task.exception)
                                Log.d("TAG",task.exception.toString())
                                if (task.exception.toString() == "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted."){
                                    showSnackBar("이메일 형식의 아이디를 입력해주세요.")
                                }
                                if (task.exception.toString() == "com.google.firebase.auth.FirebaseAuthWeakPasswordException: The given password is invalid. [ Password should be at least 6 characters ]"){
                                    showSnackBar("비밀번호를 6글자 이상으로 입력해주세요.")
                                }
                                if (task.exception.toString() == "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account."){
                                    showSnackBar("현재 이메일의 주소는 이미 사용 중 입니다.")
                                }


                            }

                    }

                }
            }


        }


    }

    override fun setValues() {
        mAuth.setLanguageCode("kr")
        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun setUpEvents() = with(binding){

        requestCertifyBtn.setOnClickListener {
            val inputPhoneNum = binding.phoneNumberArea.text.toString()
            Log.d("TAG",inputPhoneNum)

            mViewModel.setPhoneNum(inputPhoneNum)
            val phoneNum =
                "+82${inputPhoneNum.replaceFirst("0", "")}"

            phoneAuth(phoneNum)
            binding.certificationLayout.visibility = View.VISIBLE
        }

        certifyBtn.setOnClickListener {

            try {
                val phoneCredential =
                    PhoneAuthProvider.getCredential(verificationId, certifyCodeArea.text.toString())
                verifyPhoneNumWithCode(phoneCredential)

            }catch (e:Exception){

            }

        }



    }


    private fun showSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }

    private fun verifyPhoneNumWithCode(phoneAuthCredential: PhoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showSnackBar("인증 성공")
                    mViewModel.setCertify(true)
                    mAuth.currentUser?.delete()
                    mAuth.signOut()
                } else {
                    showSnackBar("인증 실패")
                    mViewModel.setCertify(false)
                }
            }
    }

    private fun phoneAuth(inputPhoneNum: String) {

        val option = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(inputPhoneNum)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)

    }

    private fun checkSignUpReady() {

        binding.joinBtn.isEnabled = mViewModel.getCertify()
    }

    private fun userPhoneListCheck(phoneNumber:String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataModel in dataSnapshot.children){

                    val user = dataModel.getValue(UserDataModel::class.java)

                    //같은 번호일 경우

                    if (user!!.phoneNumber.toString() == phoneNumber){
                        break
                    }else{
//                        usersDataList.add(user!!)
                    }


                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)

    }

    private fun uploadImage(uid: String) {

        val storage = Firebase.storage
        val storageRef = storage.reference.child(uid + ".png")


        // Get the data from an ImageView as bytes
        binding.profileImage.isDrawingCacheEnabled = true
        binding.profileImage.buildDrawingCache()
        val bitmap = (binding.profileImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }

}