package com.adams.mychatapp

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.versionedparcelable.ParcelField
import com.adams.mychatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.view.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_button.setOnClickListener {
            register()
        }

        select_photo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)


            return@setOnClickListener
        }

            already_have_an_account.setOnClickListener {
                val intent = Intent(this, loginActivity::class.java)
                startActivity(intent)
            }


    }
    var selectedPhotoUri : Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== 0 && resultCode == Activity.RESULT_OK && data!=null){
            Log.d("RegisterActivity","Photo selected")
            selectedPhotoUri =data.data
            val bitmap =MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            //val bitmap = BitmapDrawable(photoSelected)
            //circular_image.s
                select_photo.alpha = 0f
            circular_image.setImageBitmap(bitmap)
        }
    }


     private fun register(){

         val username = username_register.text.toString()
         val email = email_register.text.toString()
         val password = password_register.text.toString()
         Log.d("RegisterActivity","email is $email")
         Log.d("RegisterActivity","password is $password")
         if(email.isEmpty() || password.isEmpty()) {
             Toast.makeText(this,"please fill email/password",Toast.LENGTH_SHORT).show()
             return
             }


         FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
             .addOnCompleteListener {
                 if(!it.isSuccessful) return@addOnCompleteListener

                 Log.d("RegisterActivity","successfully created user with uid : ${it.result.user.uid}")
                 Log.d("RegisterActivity","starting upload image to database")
                 uploadImageToDatabase()
                 //Log.d("RegisterActivity","started upload image to database")

             }
             .addOnFailureListener {
                 Log.d("RegisterActivity","failed to create account : ${it.message}")
                 Toast.makeText(this,"Failed to create user, ${it.message}",Toast.LENGTH_SHORT).show()


                 return@addOnFailureListener
             }




     }

    private fun uploadImageToDatabase() {
        Log.d("RegisterActivity","started upload image to database")
        if (selectedPhotoUri==null) return
        val filename = UUID.randomUUID().toString()
     val ref=FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity","photo uploaded successfully ")
                    Toast.makeText(this,"successful",Toast.LENGTH_SHORT).show()
                    saveUserToDatabase(it.toString())
                }
                    .addOnFailureListener{
                        Log.d("RegisterActivity","failed to upload image ${it.message}")
                        Toast.makeText(this,"failed",Toast.LENGTH_SHORT).show()
                    }


            }


    }

    private fun saveUserToDatabase(profileimageurl: String) {
        val uid = FirebaseAuth.getInstance().uid
        val storage=FirebaseDatabase.getInstance().getReference("/users/$uid")
        val User= User(username_register.text.toString(),profileimageurl,uid!!)
        storage.setValue(User)
            .addOnSuccessListener {
                Log.d("RegisterActivity","user created")
                Toast.makeText(this,"successfully created new user!!!",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }

    }

}
