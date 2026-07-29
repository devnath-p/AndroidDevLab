package com.example.exp2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {
    private val TAG = "SecondActivityLifecycle"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.second_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
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
        Toast.makeText(this, "SecondActivity: $methodName", Toast.LENGTH_SHORT).show()
    }
}