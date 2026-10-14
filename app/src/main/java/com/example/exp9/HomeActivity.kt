package com.example.exp9

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * [HomeActivity] displays the home page dashboard after successful login.
 *
 * Features:
 * - Greets the authenticated user using credentials retrieved from the SQLite database.
 * - Displays a Logout button on the home screen.
 * - Handles logging out by clearing persisted SQLite credentials and returning to [MainActivity].
 *
 * @see CredentialDatabaseHelper
 * @see MainActivity
 */
class HomeActivity : AppCompatActivity() {

    // Database helper for SQLite credential management
    private lateinit var dbHelper: CredentialDatabaseHelper

    // UI View elements
    private lateinit var tvWelcomeUser: TextView
    private lateinit var btnLogout: Button

    /**
     * Called when the home activity is created. Sets up UI layout, window insets,
     * retrieves stored user information, and binds the logout action button.
     *
     * @param savedInstanceState Saved instance state bundle.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Apply system bar insets for edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SQLite Database Helper
        dbHelper = CredentialDatabaseHelper(this)

        // Bind view elements
        tvWelcomeUser = findViewById(R.id.tvWelcomeUser)
        btnLogout = findViewById(R.id.btnLogout)

        // Display welcome message using stored username
        displayWelcomeUser()

        // Configure Logout button action
        btnLogout.setOnClickListener {
            handleLogout()
        }
    }

    /**
     * Retrieves the logged-in username from intent extras or directly from the SQLite database
     * and updates the welcome text view.
     */
    private fun displayWelcomeUser() {
        val intentUsername = intent.getStringExtra(EXTRA_USERNAME)
        val username = intentUsername ?: dbHelper.getUserCredentials()?.first ?: "User"
        tvWelcomeUser.text = getString(R.string.welcome_user, username)
    }

    /**
     * Handles user logout by clearing saved credentials from SQLite database,
     * displaying a feedback toast, navigating back to [MainActivity], and finishing this activity.
     */
    private fun handleLogout() {
        // Clear stored credentials from SQLite database
        dbHelper.clearCredentials()

        // Inform user of successful logout
        Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()

        // Redirect to MainActivity (Login Screen)
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"
    }
}
