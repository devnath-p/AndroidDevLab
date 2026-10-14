package com.example.exp9

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLite database helper providing key-value pair data persistence.
 *
 * This class manages a SQLite database with a key-value schema (`credentials` table)
 * designed to store and retrieve login credentials securely on the device.
 *
 * @param context The application or activity context.
 */
class CredentialDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserCredentials.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_CREDENTIALS = "credentials"
        private const val COLUMN_KEY = "key_name"
        private const val COLUMN_VALUE = "value_text"

        // Keys for storing login information
        const val KEY_USERNAME = "saved_username"
        const val KEY_PASSWORD = "saved_password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_CREDENTIALS (
                $COLUMN_KEY TEXT PRIMARY KEY,
                $COLUMN_VALUE TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CREDENTIALS")
        onCreate(db)
    }

    /**
     * Persists a key-value pair into the SQLite database.
     *
     * @param key The unique key string identifier.
     * @param value The string value to store.
     */
    fun saveKeyValue(key: String, value: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_KEY, key)
            put(COLUMN_VALUE, value)
        }
        db.insertWithOnConflict(
            TABLE_CREDENTIALS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE,
        )
    }

    /**
     * Retrieves a stored value associated with the given key from the SQLite database.
     *
     * @param key The key string to look up.
     * @return The stored string value, or `null` if the key does not exist.
     */
    fun getKeyValue(key: String): String? {
        val db = readableDatabase
        val projection = arrayOf(COLUMN_VALUE)
        val selection = "$COLUMN_KEY = ?"
        val selectionArgs = arrayOf(key)

        val cursor = db.query(
            TABLE_CREDENTIALS,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null,
        )

        var result: String? = null
        cursor.use { c ->
            if (c.moveToFirst()) {
                val columnIndex = c.getColumnIndex(COLUMN_VALUE)
                if (columnIndex != -1) {
                    result = c.getString(columnIndex)
                }
            }
        }
        return result
    }

    /**
     * Saves user login credentials (username and password) to the SQLite key-value database.
     *
     * @param username The user's login username or email.
     * @param password The user's login password.
     */
    fun saveUserCredentials(username: String, password: String) {
        saveKeyValue(KEY_USERNAME, username)
        saveKeyValue(KEY_PASSWORD, password)
    }

    /**
     * Retrieves saved user login credentials from the SQLite database if available.
     *
     * @return A [Pair] containing (username, password), or `null` if no credentials exist.
     */
    fun getUserCredentials(): Pair<String, String>? {
        val username = getKeyValue(KEY_USERNAME)
        val password = getKeyValue(KEY_PASSWORD)

        return if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            Pair(username, password)
        } else {
            null
        }
    }

    /**
     * Clears all stored login credentials from the SQLite database.
     */
    fun clearCredentials() {
        val db = writableDatabase
        db.delete(TABLE_CREDENTIALS, null, null)
    }
}
