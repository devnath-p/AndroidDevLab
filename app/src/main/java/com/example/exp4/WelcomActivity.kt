package com.example.exp4

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val userName = intent.getStringExtra("USERNAME")

        val welcomeText = findViewById<TextView>(R.id.welcomeText)

        welcomeText.text = "Welcome, $userName!"
    }
}