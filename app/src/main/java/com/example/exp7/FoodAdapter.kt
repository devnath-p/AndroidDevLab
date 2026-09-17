package com.example.exp7

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class FoodAdapter(
    private val context: Context,
    private val names: Array<String>,
    private val descriptions: Array<String>,
    private val images: IntArray
) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return names.size
    }

    override fun getItem(position: Int): Any {
        return names[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        val view = convertView ?: inflater.inflate(
            R.layout.list_item,
            parent,
            false
        )

        val imageView = view.findViewById<ImageView>(
            R.id.itemImage
        )

        val title = view.findViewById<TextView>(
            R.id.itemTitle
        )

        val description = view.findViewById<TextView>(
            R.id.itemDescription
        )

        imageView.setImageResource(images[position])
        title.text = names[position]
        description.text = descriptions[position]

        return view
    }
}