package com.adams.mychatapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class loginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        back_to_register.setOnClickListener {
            finish()
        }
        login_button.setOnClickListener {
            login()
        }
    }
    fun login(){
        val email = email_login.text.toString()
        val password = password_login.text.toString()
        if(email.isEmpty()|| password.isEmpty()){
            Toast.makeText(this,"please enter email/password",Toast.LENGTH_SHORT).show()
              return      }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this,"Welcome",Toast.LENGTH_SHORT).show()
                        val intent= Intent(this,LatestMessagesActivity::class.java)
                        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this,"failed to login,Please enter correct details",Toast.LENGTH_SHORT).show()
                    return@addOnFailureListener

                }
        }


    }

