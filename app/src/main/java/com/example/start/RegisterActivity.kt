package com.example.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class RegisterActivity : AppCompatActivity() {
    private lateinit var  auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var user:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth= FirebaseAuth.getInstance()
        database = Firebase.database.reference
    }

    fun createBDForUser()
    {
        auth = FirebaseAuth.getInstance()
        var uid =  auth.uid!!
        database.child("users").child(uid).child("UserInfo").setValue(user)
    }

    fun register(view: View){

        val emailEdit = findViewById<EditText>(R.id.editText_email_address)
        val email=emailEdit.text.toString()

        val passwordEdit = findViewById<EditText>(R.id.editText_password)
        val password = passwordEdit.text.toString()

        val passwordRepEdit = findViewById<EditText>(R.id.editText_password_repeat)
        val passwordRep = passwordRepEdit.text.toString()

        if (password.equals(passwordRep))
        {
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if(task.isSuccessful){

                    //
                    user = User(email,null,null,null,null,null,
                          null,null,null,null)
                    createBDForUser()

                    val intent= Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
        else
        {
            Toast.makeText(applicationContext, "Passwords aren't equal!", Toast.LENGTH_SHORT).show()
        }

    }

    fun goToLogin(view: View){
        val intent= Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

}