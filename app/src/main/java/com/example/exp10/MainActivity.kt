
package com.example.exp10

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

data class Contact(
    val name: String,
    val number: String
)

class MainActivity : AppCompatActivity() {

    private lateinit var btnLoad: Button
    private lateinit var txtCount: TextView
    private lateinit var txtEmpty: TextView
    private lateinit var contactList: ListView
    private lateinit var adapter: ContactAdapter

    private val contacts = ArrayList<Contact>()

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                loadContacts()
            } else {
                Toast.makeText(
                    this,
                    "Permission denied. Cannot read contacts.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnLoad = findViewById(R.id.btnLoad)
        txtCount = findViewById(R.id.txtCount)
        txtEmpty = findViewById(R.id.txtEmpty)
        contactList = findViewById(R.id.contactList)

        adapter = ContactAdapter()
        contactList.adapter = adapter

        btnLoad.setOnClickListener {
            checkPermission()
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadContacts()
        } else {
            permissionLauncher.launch(
                Manifest.permission.READ_CONTACTS
            )
        }
    }

    private fun loadContacts() {
        contacts.clear()

        try {
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )?.use { cursor ->

                val nameIndex = cursor.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                )

                val numberIndex = cursor.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )

                while (cursor.moveToNext()) {
                    val name = cursor.getString(nameIndex)
                        ?: "Unknown"

                    val number = cursor.getString(numberIndex)
                        ?: "No number"

                    contacts.add(Contact(name, number))
                }
            }

            adapter.notifyDataSetChanged()

            txtCount.text = if (contacts.isEmpty()) {
                "No contacts found"
            } else {
                "${contacts.size} contact entries found"
            }

            txtEmpty.visibility =
                if (contacts.isEmpty()) View.VISIBLE else View.GONE

            contactList.visibility =
                if (contacts.isEmpty()) View.GONE else View.VISIBLE

            if (contacts.isEmpty()) {
                txtEmpty.text =
                    "No contacts with phone numbers found."
            }

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Unable to load contacts.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private inner class ContactAdapter :
        ArrayAdapter<Contact>(this, 0, contacts) {

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_contact, parent, false)

            val contact = getItem(position)!!

            val txtInitial = view.findViewById<TextView>(
                R.id.txtInitial
            )
            val txtName = view.findViewById<TextView>(
                R.id.txtName
            )
            val txtNumber = view.findViewById<TextView>(
                R.id.txtNumber
            )

            txtInitial.text = contact.name
                .firstOrNull()
                ?.uppercaseChar()
                ?.toString() ?: "?"

            txtName.text = contact.name
            txtNumber.text = contact.number

            return view
        }
    }
}
