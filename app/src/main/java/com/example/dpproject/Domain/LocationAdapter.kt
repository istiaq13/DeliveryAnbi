package com.example.dpproject.Adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.dpproject.domain.Location

class LocationAdapter(context: Context, private val locations: List<Location>) :
    ArrayAdapter<Location>(context, android.R.layout.simple_spinner_item, locations) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.text = locations[position].loc
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.text = locations[position].loc
        return view
    }
}