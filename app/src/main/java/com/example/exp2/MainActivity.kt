package com.example.exp2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivityLifecycle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnNavigate).setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        showLogAndToast("onCreate")
    }

    override fun onStart() {
        super.onStart()
        showLogAndToast("onStart")
    }

    override fun onResume() {
        super.onResume()
        showLogAndToast("onResume")
    }

    override fun onPause() {
        super.onPause()
        showLogAndToast("onPause")
    }

    override fun onStop() {
        super.onStop()
        showLogAndToast("onStop")
    }

    override fun onRestart() {
        super.onRestart()
        showLogAndToast("onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        showLogAndToast("onDestroy")
    }

    private fun showLogAndToast(methodName: String) {
        Log.d(TAG, "$methodName called")
        Toast.makeText(this, "MainActivity: $methodName", Toast.LENGTH_SHORT).show()
    }
}