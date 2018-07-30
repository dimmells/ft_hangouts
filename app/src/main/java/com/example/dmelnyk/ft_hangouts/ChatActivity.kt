package com.example.dmelnyk.ft_hangouts

import android.database.Cursor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.ArrayList
import android.widget.ListView
import java.lang.Thread.sleep


class ChatActivity : AppCompatActivity() {
    private var SENT = 1
    private var RECEIVED = 0
    private var contact = Contact()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        contact = intent.getSerializableExtra("contact") as Contact
        supportActionBar?.setTitle(contact.first_name + " " + contact.last_name)
        setSmsMessages()
    }

    fun selector(sms: SmsData): Date = Date(sms.date)

    private fun setSmsMessages() {
        val smsList = ArrayList<SmsData>()

        val cursor = contentResolver.query(
                Uri.parse("content://sms/inbox"),
                null,
                null,
                null,
                null
        )
        createList(cursor, smsList, RECEIVED)
        val cursorSent = contentResolver.query(
                Uri.parse("content://sms/sent"),
                null,
                null,
                null,
                null
        )
        createList(cursorSent, smsList, SENT)

        smsList.sortBy({selector(it)})
        val adapter = ListAdapter(this, smsList)
        sms_list_view.adapter = adapter
        scrollMyListViewToBottom(sms_list_view, adapter)
    }

    private fun createList(cursor: Cursor, smsList: ArrayList<SmsData>, key: Int) {
        if (cursor.moveToLast()) {
            val nameID = cursor.getColumnIndex("address")
            val messageId = cursor.getColumnIndex("body")
            val dateID = cursor.getColumnIndex("date")

            do {
                if (cursor.getString(nameID) == contact.phone_number) {
                    val dateString = cursor.getString(dateID)
                    smsList.add(SmsData(cursor.getString(nameID), Date(dateString.toLong()).toString(), cursor.getString(messageId), key))
                }
            } while (cursor.moveToPrevious())
        }

        cursor.close()
    }

    private fun scrollMyListViewToBottom(myListView: ListView, myListAdapter: ListAdapter) {
        myListView.post({
            myListView.setSelection(myListAdapter.getCount() - 1)
        })
    }

    fun sendSms(view: View) {
        SmsManager.getDefault().sendTextMessage(contact.phone_number, null, sms_text.text.toString(), null, null)
        sms_text.setText("")
        sleep(3000)
        setSmsMessages()
        Toast.makeText(this, "SMS sent", Toast.LENGTH_SHORT).show()
    }
}
