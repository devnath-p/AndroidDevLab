package com.example.exp3

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment

class DetailFragment : Fragment(R.layout.fragment_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView = view.findViewById<TextView>(R.id.txtDetail)

        val course = arguments?.getString("course") ?: "No Course Selected"

        val details = when (course) {
            "Android" -> "Android is Google's mobile operating system."
            "Java" -> "Java is a popular object-oriented programming language."
            "Python" -> "Python is widely used for AI, ML and Web Development."
            "Flutter" -> "Flutter is Google's cross-platform UI toolkit."
            else -> "Select a course from the list."
        }

        textView.text = details
    }

    companion object {

        fun newInstance(course: String): DetailFragment {

            val fragment = DetailFragment()

            val bundle = Bundle()
            bundle.putString("course", course)

            fragment.arguments = bundle

            return fragment
        }
    }
}