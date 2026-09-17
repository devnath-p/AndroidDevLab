package com.example.exp7

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.listView)

        val names = arrayOf(
            "Image 1",
            "Image 2",
            "Image 3",
            "Image 4"
        )

        val descriptions = arrayOf(
            "First image",
            "Second image",
            "Third image",
            "Fourth image"
        )

        val images = intArrayOf(
            R.drawable.img1,
            R.drawable.img2,
            R.drawable.img3,
            R.drawable.img4
        )

        val adapter = FoodAdapter(
            this,
            names,
            descriptions,
            images
        )

        listView.adapter = adapter
    }
}