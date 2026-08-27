package com.example.exp6

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var checkTerms: CheckBox
    private lateinit var toggleNotifications: ToggleButton
    private lateinit var btnRegister: Button
    private lateinit var btnProfile: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Connect XML views with Kotlin
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        checkTerms = findViewById(R.id.checkTerms)
        toggleNotifications = findViewById(R.id.toggleNotifications)
        btnRegister = findViewById(R.id.btnRegister)
        btnProfile = findViewById(R.id.btnProfile)

        // ImageButton
        btnProfile.setOnClickListener {
            Toast.makeText(
                this,
                "Profile image button clicked",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Register Button
        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {

        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate name
        if (name.isEmpty()) {
            etName.error = "Please enter your name"
            etName.requestFocus()
            return
        }

        // Validate email
        if (email.isEmpty()) {
            etEmail.error = "Please enter your email"
            etEmail.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Enter a valid email address"
            etEmail.requestFocus()
            return
        }

        // Validate password
        if (password.isEmpty()) {
            etPassword.error = "Please enter your password"
            etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            etPassword.error = "Password must contain at least 6 characters"
            etPassword.requestFocus()
            return
        }

        // Validate gender
        val selectedGenderId = radioGroupGender.checkedRadioButtonId

        if (selectedGenderId == -1) {
            Toast.makeText(
                this,
                "Please select your gender",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Validate terms
        if (!checkTerms.isChecked) {
            Toast.makeText(
                this,
                "Please accept the Terms & Conditions",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Notification status
        val notificationStatus =
            if (toggleNotifications.isChecked) {
                "Enabled"
            } else {
                "Disabled"
            }

        // Registration successful
        AlertDialog.Builder(this)
            .setTitle("Registration Successful")
            .setMessage(
                "Welcome, $name!\n\n" +
                        "Email: $email\n" +
                        "Notifications: $notificationStatus"
            )
            .setPositiveButton("OK", null)
            .show()
    }
}