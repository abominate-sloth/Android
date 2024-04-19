package com.example.start

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class ProfileActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var uid = ""

    lateinit var nicknameEdit:EditText
    lateinit var emailTextView:TextView
    lateinit var nameEdit :EditText
    lateinit var surnameEdit:EditText
    lateinit var ageEdit:EditText
    lateinit var genderEdit:EditText
    lateinit var phoneEdit:EditText
    lateinit var countryEdit:EditText
    lateinit var birthdateEdit:EditText
    lateinit var favFishEdit:EditText

    private var userPub = User("","","","","","",
                               "","","","")

    fun findElements()
    {
        nicknameEdit = findViewById<EditText>(R.id.profile_nickname)
        emailTextView = findViewById<TextView>(R.id.profile_email)
        nameEdit = findViewById<EditText>(R.id.profile_name)
        surnameEdit = findViewById<EditText>(R.id.profile_surname)
        ageEdit = findViewById<EditText>(R.id.profile_age)
        genderEdit = findViewById<EditText>(R.id.profile_gender)
        phoneEdit = findViewById<EditText>(R.id.profile_phone)
        countryEdit = findViewById<EditText>(R.id.profile_country)
        birthdateEdit = findViewById<EditText>(R.id.profile_birthdate)
        favFishEdit = findViewById<EditText>(R.id.profile_favfish)
    }

    fun setUserValue(user:User)
    {
        nicknameEdit.setText(user.nickname)
        emailTextView.text = user.email
        nameEdit.setText(user.name)
        surnameEdit.setText(user.surname)
        ageEdit.setText(user.age)
        genderEdit.setText(user.gender)
        phoneEdit.setText(user.phone)
        countryEdit.setText(user.country)
        birthdateEdit.setText(user.birthDate)
        favFishEdit.setText(user.favoriteFish)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        findElements()

        firebaseAuth = FirebaseAuth.getInstance()
        uid = firebaseAuth.uid!!
        database = Firebase.database.reference

        var ref = database.child("users").child(uid).child("UserInfo")

        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val user = dataSnapshot.getValue<User>()
                if (user != null) {
                    setUserValue(user)
                }
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        ref.addValueEventListener(userListener)

        val btnProfSave: Button = findViewById(R.id.profile_savebutton)
        val btnLogOut: Button = findViewById(R.id.profile_profile_log_out)
        val btnDelete: Button = findViewById(R.id.profile_profile_delete)

        btnProfSave.setOnClickListener{
            userPub.nickname = nicknameEdit.text.toString()
            userPub.email = emailTextView.text.toString()
            userPub.surname =surnameEdit.text.toString()
            userPub.name = nameEdit.text.toString()
            userPub.age = ageEdit.text.toString()
            userPub.gender = genderEdit.text.toString()
            userPub.phone = phoneEdit.text.toString()
            userPub.country = countryEdit.text.toString()
            userPub.birthDate = birthdateEdit.text.toString()
            userPub.favoriteFish = favFishEdit.text.toString()

            database.child("users").child(uid).child("UserInfo").setValue(userPub)
        }

        btnLogOut.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            firebaseAuth.signOut()
            startActivity(intent)
            finish()
        }

        btnDelete.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)

            val auth:FirebaseAuth = Firebase.auth
            val user = auth.currentUser
            if(user != null)
            {
                auth.signOut();
            }

            database.child("users").child(uid).removeValue()
                .addOnSuccessListener {
                    user?.delete()
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { error ->
                    // Обработка ошибки при удалении записи
                }
        }
    }
}