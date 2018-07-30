package com.example.dmelnyk.ft_hangouts

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.contact_dialog_view.view.*
import android.graphics.BitmapFactory
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.photo_source_dialog.view.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private var IMAGE_DIRECTORY = "/ft_hangouts_image/"
    private var THEME = "theme"
    private var RED = "red"
    private var GREEN = "green"

    private var permissions = arrayOf(Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS)
    var contact = Contact()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (getPreferences(Context.MODE_PRIVATE).getString(THEME, "") == GREEN)
            setTheme(R.style.AppThemeGreen)
        else if (getPreferences(Context.MODE_PRIVATE).getString(THEME, "") == RED)
            setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for (i in permissions) {
            if (ContextCompat.checkSelfPermission(this@MainActivity,
                            i) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(permissions, 10)
                }
            }
        }
        add_contact.setOnClickListener { view ->
            val intentAddContact = Intent(this, AddContactActivity::class.java)
            startActivity(intentAddContact)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        readContact()
    }

    fun openChat(view: View) {
        startActivity(Intent(this, ChatActivity::class.java))
    }

    fun readContact() {
        var db = DBHandler(this)
        var data = db.getAllContact()

        if (data.size == 0)
            return
        no_contact.text = ""
        var list: MutableList<View> = ArrayList()
        if (list.size > 0)
            for (i in 0..(data.size - 1))
                list.removeAt(i)

        val lyt = findViewById(R.id.list_contact) as LinearLayout
        lyt.removeAllViews()
        for (i in 0..(data.size - 1)) {
            list.add(LayoutInflater.from(this).inflate(R.layout.contact_dialog_view, null, false))
            list.get(i).name.text = data.get(i).first_name + " " + data.get(i).last_name

            val path = ((android.os.Environment.getExternalStorageDirectory()).toString())
            val imgFile = File(path.plus(IMAGE_DIRECTORY).plus(data.get(i).photo))

            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
                list.get(i).contact_photo_view.setImageBitmap(myBitmap)

            }
        }
        for (i in 0..(list.size - 1)) {
            list_contact.addView(list.get(i))
            list.get(i).contact_photo_view.setOnClickListener {
                contact.id = data.get(i).id
                contact.first_name = data.get(i).first_name
                contact.last_name = data.get(i).last_name
                contact.phone_number = data.get(i).phone_number
                contact.email = data.get(i).email
                contact.photo = data.get(i).photo

                val intent = Intent(this, ContactActivity::class.java)
                intent.putExtra("contact", contact);
                startActivity(intent)
                finish()
            }

            list.get(i).contact_message.setOnClickListener {
                contact.id = data.get(i).id
                contact.first_name = data.get(i).first_name
                contact.last_name = data.get(i).last_name
                contact.phone_number = data.get(i).phone_number
                contact.email = data.get(i).email
                contact.photo = data.get(i).photo

                val intentChat = Intent(this, ChatActivity::class.java)
                intentChat.putExtra("contact", contact)
                startActivity(intentChat)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.set_color) {
            val dialog = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.photo_source_dialog, null)
            dialogView.question.text = "Choose theme"
            dialogView.dialog_camera.text = "Red"
            dialogView.dialog_gallery.text = "Green"
            dialogView.dialog_camera.setOnClickListener {applyRed()}
            dialogView.dialog_gallery.setOnClickListener {applyGreen()}
            dialog.setView(dialogView)
            dialog.show()
        }
        return true
    }

    private fun applyGreen() {
        var ed: SharedPreferences.Editor = getPreferences(Context.MODE_PRIVATE).edit()
        ed.putString(THEME, GREEN)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    fun applyRed() {
        var ed: SharedPreferences.Editor = getPreferences(Context.MODE_PRIVATE).edit()
        ed.putString(THEME, RED)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
