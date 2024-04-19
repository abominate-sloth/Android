package com.example.start

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(var items:ArrayList<ByteArray>, var context: Context):RecyclerView.Adapter<ImageAdapter.myViewHolder>() {

    class myViewHolder(view: View): RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.desc_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_describe_design, parent, false)
        return myViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

        if(items[position]!=null){
            val bitmap = BitmapFactory.decodeByteArray(
                items[position],
                0,
                items[position].size
            )
            holder.image.setImageBitmap(bitmap)
        }

    }
}