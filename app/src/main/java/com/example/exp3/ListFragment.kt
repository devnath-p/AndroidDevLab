package com.example.exp3

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class ListFragment : Fragment(R.layout.fragment_list) {

    private lateinit var listener: OnItemSelectedListener

    private val courses = arrayOf(
        "Android",
        "Java",
        "Python",
        "Flutter"
    )

    interface OnItemSelectedListener {
        fun onItemSelected(course: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = context as OnItemSelectedListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.listView)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            courses
        )

        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->

            val selectedCourse = courses[position]

            listener.onItemSelected(selectedCourse)
        }
    }
}