package com.example.exp4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val username = findViewById<TextInputEditText>(R.id.username)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {

            val userName = username.text.toString()

            if (userName.isNotEmpty()) {

                val intent = Intent(this, WelcomeActivity::class.java)

                intent.putExtra("USERNAME", userName)

                startActivity(intent)

            } else {
                Toast.makeText(
                    this,
                    "Please enter your username",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}