package com.example.labexam

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SummaryActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvCourseName: TextView
    private lateinit var tvRecommendation: TextView
    private lateinit var ratingGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvCourseName = findViewById(R.id.tvCourseName)
        tvRecommendation = findViewById(R.id.tvRecommendation)
        ratingGroup = findViewById(R.id.ratingGroup)

        // Get data from Intent
        val name = intent.getStringExtra("name")
        val email = intent.getStringExtra("email")
        val courseName = intent.getStringExtra("courseName")
        val rating = intent.getStringExtra("rating")
        val recommendation = intent.getStringExtra("recommendation")

        // Display student details
        tvName.text = "Student Name: $name"
        tvEmail.text = "Email: $email"
        tvCourseName.text = "Course Name: $courseName"

        // Select submitted rating
        when (rating) {

            "Excellent" -> {
                ratingGroup.check(R.id.summaryExcellent)
            }

            "Good" -> {
                ratingGroup.check(R.id.summaryGood)
            }

            "Average" -> {
                ratingGroup.check(R.id.summaryAverage)
            }

            "Poor" -> {
                ratingGroup.check(R.id.summaryPoor)
            }
        }

        // Disable rating buttons
        for (i in 0 until ratingGroup.childCount) {
            ratingGroup.getChildAt(i).isEnabled = false
        }

        // Display recommendation
        tvRecommendation.text =
            "Recommend Course: $recommendation"
    }
}