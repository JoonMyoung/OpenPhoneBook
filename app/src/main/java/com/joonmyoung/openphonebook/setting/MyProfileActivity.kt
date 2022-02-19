package com.joonmyoung.openphonebook.setting

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.joonmyoung.openphonebook.R
import com.joonmyoung.openphonebook.auth.UserDataModel
import com.joonmyoung.openphonebook.databinding.ActivityMyProfileBinding
import com.joonmyoung.openphonebook.utils.FirebaseAuthUtils
import com.joonmyoung.openphonebook.utils.FirebaseRef
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.Exception

class MyProfileActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMyProfileBinding.inflate(layoutInflater)
    }
    private val uid = FirebaseAuthUtils.getUid()
    private var phoneNumber = ""
    private var email = ""
    private var nickname = ""
    private var token = ""
    private var comment = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var compareImage : Boolean = true

        getMydata()

        val getAction = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback { uri ->
                binding.myProfileImageView.setImageURI(uri)
            }
        )

        binding.myProfileCancelBtn.setOnClickListener {
            finish()
        }

        binding.myProfileImageView.setOnClickListener {
            getAction.launch("image/*")
        }


        binding.myProfileRepairBtn.setOnClickListener {

            val nicknameCheck = binding.myProfileNicknameEdit.text.toString()
            val commentCheck = binding.myProfileMessageEdit.text.toString()

            if (nicknameCheck.isEmpty()){
                showSnackBar("닉네임을 입력 해 주세요.")
            }else{
                try {
                    val bitmap = binding.myProfileImageView.drawable.toBitmap()
                    val bitmap2 = binding.myProfileImageView2.drawable.toBitmap()
                    compareImage = compare(bitmap,bitmap2)

                    if (compareImage == true){
                        Toast.makeText(this, "사진없음", Toast.LENGTH_SHORT).show()


                    }else{
                        deleteImage(uid)
                    }

                }catch (e: Exception){
                    Toast.makeText(this, "사진을 등록해 주세요", Toast.LENGTH_SHORT).show()

                }

                val userModel = UserDataModel(uid, email, phoneNumber, nicknameCheck, commentCheck, token)
                FirebaseRef.userInfoRef.child(uid).setValue(userModel)

                finish()


            }

        }

    }

    private fun compare(b1: Bitmap, b2: Bitmap): Boolean {
        return if (b1.width == b2.width && b1.height == b2.height) {
            val pixels1 = IntArray(b1.width * b1.height)
            val pixels2 = IntArray(b2.width * b2.height)
            b1.getPixels(pixels1, 0, b1.width, 0, 0, b1.width, b1.height)
            b2.getPixels(pixels2, 0, b2.width, 0, 0, b2.width, b2.height)
            Arrays.equals(pixels1, pixels2)
        } else {
            false
        }
    }

    private fun deleteImage(uid:String){

        val storageRef = Firebase.storage.reference
        val desertRef = storageRef.child(uid+ ".png")


        desertRef.delete().addOnSuccessListener {
            repairImage()

        }.addOnFailureListener {
            repairImage()
        }

    }

    private fun repairImage(){

        val storage = Firebase.storage
        val storageRef = storage.reference.child(uid + ".png")


        // Get the data from an ImageView as bytes
        binding.myProfileImageView.isDrawingCacheEnabled = true
        binding.myProfileImageView.buildDrawingCache()
        val bitmap = (binding.myProfileImageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = storageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            finish()
        }
    }

    private fun getMydata() {


        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d("TAG", dataSnapshot.toString())
                val data = dataSnapshot.getValue(UserDataModel::class.java)


                binding.myProfileNicknameEdit.setText(data!!.nickname)
                binding.myProfileMessageEdit.setText(data!!.comment)

                phoneNumber = data.phoneNumber.toString()
                email = data.email.toString()
                nickname = data.nickname.toString()
                token = data.token.toString()
                comment = data.comment.toString()

                try {
                    val storageRef = Firebase.storage.reference.child(data.uid + ".png")
                    storageRef.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->

                        if (task.isSuccessful) {
                            Glide.with(baseContext).load(task.result).into(binding.myProfileImageView)
                            Glide.with(baseContext).load(task.result).into(binding.myProfileImageView2)
                        }

                    })

                }catch (e:Exception){
                    Toast.makeText(this@MyProfileActivity, "사진로드실패", Toast.LENGTH_SHORT).show()
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.child(uid).addValueEventListener(postListener)
    }


    private fun showSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }


}


