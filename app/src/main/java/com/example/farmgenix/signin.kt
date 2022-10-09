package com.example.farmgenix

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signin.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class signin : AppCompatActivity() {
   private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        btnSignup.setOnClickListener {
            createAccount()
        }
        text_one_donttwob.setOnClickListener {
            startActivity(Intent(this,signup::class.java))
        }


    }

    private fun createAccount() {
        val fullname = etEmaill.text.toString()
        val email = email.text.toString()
        val password = mali.text.toString()
        val confirmpassword = etEmai.text.toString()

        when{
            TextUtils.isEmpty(fullname) -> Toast.makeText(this,"FullName is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this,"Email is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"Password is required", Toast.LENGTH_LONG).show()

            else -> {

                val progressDialog = ProgressDialog(this@signin)
                progressDialog.setTitle("Sign Up")
                progressDialog.setMessage("Please wait, this might take a while...")
                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()


                mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                        {

                            saveUserInfo(fullname,email,progressDialog)
                        }
                        else
                        {
                            val message = task.exception!!.toString()
                            Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }

    }

    private fun saveUserInfo(fullname: String, email: String, progressDialog: ProgressDialog) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val timeformat = SimpleDateFormat("dd/M/yyyy")
        val date: Date = Date()
        val strDate = timeformat.format(date).toString()
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["fullname"] = fullname
        userMap["email"] = email
        userMap["uid"] = currentUserID
        userMap["time"] = strDate
        userMap["image"] = "gs://jobstock-17213.appspot.com/Default Images/person.png"
//
        usersRef.child(currentUserID).setValue(userMap)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val notRef = FirebaseDatabase.getInstance().reference.child("Notifications").child(firebaseUser.uid)

        val commentsMap = HashMap<String, Any>()
        commentsMap["notification"] = "welcome to kabarak connect"
        commentsMap["publisher"] = firebaseUser!!.uid
        commentsMap["image"] = ""

        notRef.push().setValue(commentsMap)

            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {


                    progressDialog.dismiss()

                    val intent = Intent(this@signin, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
//                    naneanimationme.visibility = View.VISIBLE
//                    CoroutineScope(Dispatchers.Main).launch {
//                        delay(5500L)
                    startActivity(Intent(this@signin,MainActivity::class.java))
//                    }
                    finish()

                }
                else
                {
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Error: $message",Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }

    }


}