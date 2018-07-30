package com.example.dmelnyk.ft_hangouts

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.received_sms.view.*
import kotlinx.android.synthetic.main.row_layout.view.*

class ListAdapter(val context:Context, val list: ArrayList<SmsData>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        if (list[position].key == 1) {
            val view = LayoutInflater.from(context).inflate(R.layout.sent_sms, parent, false)

//            view.sms_sender.text = list[position].senderName
//            view.sms_date.text = list[position].date
            view.sms_message.text = list[position].message

            return view
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.received_sms, parent, false)

//            view.sms_sender.text = list[position].senderName
//            view.sms_date.text = list[position].date
            view.sms_message_res.text = list[position].message

            return view
        }
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }
}