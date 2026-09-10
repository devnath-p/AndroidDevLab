package com.example.labexam

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCourseName: EditText
    private lateinit var radioGroup: RadioGroup
    private lateinit var toggleRecommend: ToggleButton
    private lateinit var cbTerms: CheckBox
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etCourseName = findViewById(R.id.etCourseName)
        radioGroup = findViewById(R.id.radioGroup)
        toggleRecommend = findViewById(R.id.toggleRecommend)
        cbTerms = findViewById(R.id.cbTerms)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val courseName = etCourseName.text.toString().trim()

            // Name validation
            if (name.isEmpty()) {
                etName.error = "Enter your name"
                etName.requestFocus()
                return@setOnClickListener
            }

            // Email validation
            if (email.isEmpty()) {
                etEmail.error = "Enter your email"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Enter a valid email address"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            // Course name validation
            if (courseName.isEmpty()) {
                etCourseName.error = "Enter course name"
                etCourseName.requestFocus()
                return@setOnClickListener
            }

            // Rating validation
            val selectedId = radioGroup.checkedRadioButtonId

            if (selectedId == -1) {
                Toast.makeText(
                    this,
                    "Please select a rating",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Get rating
            val rating = when (selectedId) {
                R.id.rbExcellent -> "Excellent"
                R.id.rbGood -> "Good"
                R.id.rbAverage -> "Average"
                R.id.rbPoor -> "Poor"
                else -> ""
            }

            // Get recommendation
            val recommendation = if (toggleRecommend.isChecked) {
                "Yes"
            } else {
                "No"
            }

            // Terms validation
            if (!cbTerms.isChecked) {
                Toast.makeText(
                    this,
                    "Please accept the terms and conditions",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Intent
            val intent = Intent(
                this,
                SummaryActivity::class.java
            )

            intent.putExtra("name", name)
            intent.putExtra("email", email)
            intent.putExtra("courseName", courseName)
            intent.putExtra("rating", rating)
            intent.putExtra("recommendation", recommendation)

            // Form submitted notification
            Toast.makeText(
                this,
                "Form submitted successfully!",
                Toast.LENGTH_SHORT
            ).show()

            startActivity(intent)
        }
    }
}