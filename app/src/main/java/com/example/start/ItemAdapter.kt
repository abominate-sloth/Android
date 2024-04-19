package com.example.start

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import kotlin.system.exitProcess

class ItemAdapter(var items:List<Item>, var context: Context):RecyclerView.Adapter<ItemAdapter.myViewHolder>() {

    class myViewHolder(view: View): RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.item_list_image)
        val title: TextView = view.findViewById(R.id.item_list_title)
        val desc: TextView = view.findViewById(R.id.item_list_desc)
        val btn: Button =  view.findViewById(R.id.item_list_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_design, parent, false)
        return myViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

        if(items[position].images!=null) {
            val bitmap = BitmapFactory.decodeByteArray(
                items[position].images?.get(0),
                0,
                items[position].images?.get(0)!!.size
            )
            holder.image.setImageBitmap(bitmap)
        }
        holder.title.text = items[position].title
        holder.desc.text = items[position].desc

       // val bitmap = BitmapFactory.decodeByteArray(imageList[0], 0, imageList[0].size)
       // holder.image.setImageBitmap(bitmap)

        holder.btn.setOnClickListener{
            val intent = Intent(context, DescribeActivity::class.java)
            intent.putExtra("Title", items[position].title)
            intent.putExtra("Text", items[position].text)
            context.startActivity(intent)
        }

    }
}