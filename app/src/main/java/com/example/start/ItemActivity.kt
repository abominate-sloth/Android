package com.example.start

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.integrity.internal.al
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import org.checkerframework.checker.nullness.qual.NonNull


class ItemActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var uid = ""

    private var isNotFav = true
    private lateinit var itemsList: RecyclerView
    private val items = arrayListOf<Item>()
    private var fullItems = arrayListOf<Item>()

    private lateinit var refFish : DatabaseReference

    private fun showList()
    {
        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = ItemAdapter(items, this)
    }

    private fun chekFav(item:Item) : Boolean
    {
        var isFav = false

        var ref = Firebase.database.reference.child("users").child(uid).child("Favorites").child(item.title.toString())

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Обработка полученных данных
                val value = dataSnapshot.getValue(Boolean::class.java)
                if (value != null) {
                    isFav = value
                }

                if(isFav)
                {
                    items.add(item)
                    showList()
                }


            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })


        return true
    }

    private fun makeList()
    {
        var ok = true

        for(item in fullItems) {
            if(item.images==null)
                ok = false
        }

        if(ok) {
            if (isNotFav) {
                items.clear()
                items.addAll(fullItems)
            } else {
                items.clear()
                for(item in fullItems)
                {
                    chekFav(item)
                }
            }
            showList()
        }
    }

    fun getFish(title:String,callback: (ArrayList<ByteArray>) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("FishImages").child(title) // Укажите путь к вашей папке в Firebase Storage

        var imageList= ArrayList<ByteArray>()
        val maxDownloadSizeBytes: Long = 2048 * 2048
        storageRef.listAll().addOnSuccessListener { task ->
            var reference = task.items[0]
                try {
                    reference.getBytes(maxDownloadSizeBytes).addOnCompleteListener{task->
                        imageList.add(task.result)
                        callback(imageList)
                    }
                }catch(e:Exception)
                {
                    println(e.toString())
                }
        }
    }

    private fun listenFish()
    {
        refFish = database.child("fishBook")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                fullItems.clear()

                for (idSnapshot in dataSnapshot.children) {

                    idSnapshot?.let { snapshot ->
                        val title = snapshot.child("title").getValue(String::class.java)
                        val desc = snapshot.child("desc").getValue(String::class.java)
                        val text = snapshot.child("text").getValue(String::class.java)

                       // Toast.makeText(applicationContext, snapshot.ref.key, Toast.LENGTH_SHORT).show()

                        if (title != null && desc != null && text != null) {
                        //    Toast.makeText(applicationContext, snapshot.ref.key + "\nreaded", Toast.LENGTH_SHORT).show()
                            val item = Item(title, desc, text)
                            fullItems.add(item)
                        }
                    }
                }

                //

                for(item in fullItems)
                {
                    getFish(item.title!!){imageList ->
                        item.images = imageList
                        makeList()
                    }

                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок чтения данных
            }
        }
        refFish.addValueEventListener(eventListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        itemsList = findViewById(R.id.recycleView)

        database = Firebase.database.reference
        firebaseAuth = FirebaseAuth.getInstance()
        uid = firebaseAuth.uid!!

        listenFish()

        val btnProf: Button = findViewById(R.id.button_to_userProfile)
        val btnFav: Button = findViewById(R.id.button_fav)

        btnProf.setOnClickListener{
            val intent= Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }

        btnFav.setOnClickListener{
            isNotFav = !isNotFav
            makeList()
        }
        
    }


}