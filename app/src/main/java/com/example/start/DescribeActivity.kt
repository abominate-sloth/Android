package com.example.start

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Profile
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.Serializable


class DescribeActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var uid = ""
    private lateinit var itemsList: RecyclerView
    lateinit var title: TextView
    lateinit var text: TextView

    fun getFish(title:String,callback: (ArrayList<ByteArray>) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("FishImages").child(title) // Укажите путь к вашей папке в Firebase Storage

        var imageList= ArrayList<ByteArray>()
        var referencesList:List<StorageReference> = emptyList();
        val maxDownloadSizeBytes: Long = 2048 * 2048;
        storageRef.listAll().addOnSuccessListener { task ->
            referencesList = task.items;
            for(reference in referencesList)
            {
                try {
                    reference.getBytes(maxDownloadSizeBytes).addOnCompleteListener{task->
                        imageList.add(task.result)
                        if(imageList.size == referencesList.size) {
                            callback(imageList)
                        }
                    }
                }catch(e:Exception)
                {
                    println(e.toString());
                }
            }

        }
    }

    fun makeFav()
    {
        var isFav = false

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Обработка полученных данных
                val value = dataSnapshot.getValue(Boolean::class.java)
                if (value != null) {
                    isFav = value
                }
                database.setValue(!isFav)
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_describe)

        title = findViewById(R.id.describe_title)
        text = findViewById(R.id.describe_text)
        itemsList = findViewById(R.id.describe_image_list)


        title.text = intent.getStringExtra("Title")
        text.text = intent.getStringExtra("Text")

        firebaseAuth = FirebaseAuth.getInstance()
        uid = firebaseAuth.uid!!
        database = Firebase.database.reference.child("users").child(uid).child("Favorites").child(title.text.toString())


        var images = arrayListOf<ByteArray>()

        getFish(title.text.toString()) { imageList ->
            images = imageList
            itemsList.layoutManager = LinearLayoutManager(this)
            itemsList.adapter = ImageAdapter(images,this)
        }

        val btnFav: Button = findViewById(R.id.describe_button)

        btnFav.setOnClickListener{
            makeFav()
        }

    }
}