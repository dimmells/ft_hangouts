package com.example.dmelnyk.ft_hangouts

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_contact.*
import kotlinx.android.synthetic.main.contact_data.*

class ContactActivity : AppCompatActivity() {

    private var contact = Contact()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        setSupportActionBar(toolbar)

        getInfo()

        delete_contact.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.delete_contact_dialog, null)
            dialog.setView(dialogView)
            dialog.show()
        }

        edit_contact.setOnClickListener {
            startEditActivity()
        }

        fab.setOnClickListener {
            val intentChat = Intent(this, ChatActivity::class.java)
            intentChat.putExtra("contact", contact)
            startActivity(intentChat)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun deleteContact(view: View) {
        val db = DBHandler(this)
        db.deleteContact(contact.id)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        Toast.makeText(this, "Contact has been deleted", Toast.LENGTH_LONG).show()
        finish()
    }

    fun closeDialog(view: View) {
        finish()
    }

    fun startEditActivity() {
        val intent = Intent(this, EditContactActivity::class.java)
        intent.putExtra("contact", contact)
        startActivity(intent)
        finish()
    }

    fun getInfo() {
        contact = intent.getSerializableExtra("contact") as Contact
        supportActionBar?.setTitle(contact.first_name + " " + contact.last_name)
        contact_number.text = contact.phone_number
        contact_email.text = contact.email
    }
}
