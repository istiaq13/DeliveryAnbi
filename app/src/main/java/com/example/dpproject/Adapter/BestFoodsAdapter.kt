/*package com.example.dpproject.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project162.Domain.Foods

class BestFoodsAdapter(private val items: ArrayList<Foods>) : RecyclerView.Adapter<BestFoodsAdapter.ViewHolder>() {

    // onCreateViewHolder is used to inflate the view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context: Context = parent.context
        val inflate: View = LayoutInflater.from(context).inflate(R.layout.viewholder_best_deal, parent, false)
        return ViewHolder(inflate)
    }

    // onBindViewHolder binds data to the view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val foodItem = items[position]
        holder.titleTxt.text = foodItem.title
        holder.priceTxt.text = foodItem.price
        holder.starTxt.text = foodItem.star
        holder.timeTxt.text = foodItem.time
        // Set image using Picasso or Glide (if needed)
        // holder.pic.setImageResource(foodItem.imageResource)
    }

    // Returns the number of items in the list
    override fun getItemCount(): Int = items.size

    // ViewHolder holds the views for each item
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTxt: TextView = itemView.findViewById(R.id.titleTxt)
        val priceTxt: TextView = itemView.findViewById(R.id.priceTxt)
        val starTxt: TextView = itemView.findViewById(R.id.starTxt)
        val timeTxt: TextView = itemView.findViewById(R.id.timeTxt)
        val pic: ImageView = itemView.findViewById(R.id.pic)
    }
}
*/