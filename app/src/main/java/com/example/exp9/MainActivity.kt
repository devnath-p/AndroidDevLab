package com.example.exp9

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText

/**
 * [MainActivity] serves as the login user interface.
 *
 * This activity handles:
 * - Displaying a login screen where users enter their credentials.
 * - Instantly persisting login credentials (username and password) into a key-value SQLite database ([CredentialDatabaseHelper]) as the user types, before clicking login.
 * - Automatically populating ("auto-pasting") saved credentials when the app launches or process restarts.
 * - Redirecting the user to [HomeActivity] upon clicking the login button.
 *
 * @see CredentialDatabaseHelper
 * @see HomeActivity
 */
class MainActivity : AppCompatActivity() {

    // Database helper for SQLite key-value persistence
    private lateinit var dbHelper: CredentialDatabaseHelper

    // UI View elements
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvAutoFillBanner: TextView

    /**
     * Called when the activity is starting. Initializes UI components, sets up edge-to-edge
     * window insets, auto-pastes saved credentials from SQLite, and configures instant auto-save
     * listeners on text input fields.
     *
     * @param savedInstanceState If non-null, this activity is being re-constructed from a previous saved state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Apply system bar window insets for edge-to-edge layout styling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SQLite Database Helper
        dbHelper = CredentialDatabaseHelper(this)

        // Bind view components
        initViews()

        // Auto-fill saved credentials from SQLite database if previously typed/saved
        autoPasteSavedCredentials()

        // Configure instant real-time persistence as the user types (before clicking login)
        setupInstantAutoSave()

        // Set up click listener for Login button
        btnLogin.setOnClickListener {
            handleLogin()
        }
    }

    /**
     * Initializes and binds UI view elements from the layout.
     */
    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvAutoFillBanner = findViewById(R.id.tvAutoFillBanner)
    }

    /**
     * Retrieves saved credentials from the SQLite key-value database and auto-fills ("auto-pastes")
     * them into the username and password fields automatically upon activity creation.
     */
    private fun autoPasteSavedCredentials() {
        val credentials = dbHelper.getUserCredentials()
        if (credentials != null) {
            val (savedUsername, savedPassword) = credentials
            etUsername.setText(savedUsername)
            etPassword.setText(savedPassword)

            // Display auto-fill notice banner
            tvAutoFillBanner.visibility = View.VISIBLE
        } else {
            tvAutoFillBanner.visibility = View.GONE
        }
    }

    /**
     * Attaches text change listeners to [etUsername] and [etPassword] so that credentials
     * are saved instantly into SQLite in real-time as the user types, before clicking Login.
     */
    private fun setupInstantAutoSave() {
        val saveCredentialsAction = {
            val username = etUsername.text?.toString() ?: ""
            val password = etPassword.text?.toString() ?: ""

            // Instantly persist current input in SQLite database
            dbHelper.saveUserCredentials(username, password)

            // Update auto-fill banner status
            if (username.isNotEmpty() || password.isNotEmpty()) {
                tvAutoFillBanner.visibility = View.VISIBLE
            } else {
                tvAutoFillBanner.visibility = View.GONE
            }
        }

        etUsername.doAfterTextChanged {
            saveCredentialsAction()
        }

        etPassword.doAfterTextChanged {
            saveCredentialsAction()
        }
    }

    /**
     * Handles user login action. Validates that fields are not empty, ensures credentials are
     * persisted in SQLite database, and redirects the user to [HomeActivity].
     */
    private fun handleLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show()
            return
        }

        // Guarantee final credentials state is persisted in SQLite
        dbHelper.saveUserCredentials(username, password)

        // Provide feedback to the user
        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()

        // Redirect to HomeActivity
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra(HomeActivity.EXTRA_USERNAME, username)
        }
        startActivity(intent)
        finish()
    }
}
