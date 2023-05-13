package com.ssafy.smartstore.alarm

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ssafy.smartstore.R
import com.ssafy.smartstore.dto.FCMMessage
import com.ssafy.smartstore.network.RetroApp
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AlarmAdapter(val context: Context, val resources: Int, val objects: ArrayList<FCMMessage>) :
    RecyclerView.Adapter<AlarmViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(resources, parent, false)

        return AlarmViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val date = Date(objects[position].timestamp)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm", Locale("ko", "KR"))

        holder.title.text = objects[position].title
        holder.body.text = objects[position].body
        holder.time.text = dateFormat.format(date)

        if (position == objects.size - 1) {
            holder.divline.visibility = View.GONE
        }

        holder.btnDelete.setOnClickListener {
            val mDialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog, null)
            val mBuilder = AlertDialog.Builder(context)
                .setView(mDialogView)

            mDialogView.findViewById<TextView>(R.id.dialog_title).text = "정말 삭제하시겠습니까?"

            val mAlertDialog = mBuilder.show()

            mDialogView.findViewById<Button>(R.id.Confirm).setOnClickListener {
                val sharedPreference = RetroApp
                val gson = GsonBuilder().create()
                val data = FCMMessage(
                    objects[position].title,
                    objects[position].body,
                    objects[position].timestamp
                )
                val tempArray = ArrayList<FCMMessage>()
                val groupListType: Type = object : TypeToken<ArrayList<FCMMessage?>?>() {}.type
                val prev = sharedPreference.prefs.getString("messageList", "none")
                if (prev != "[]" || prev != "") {
                    tempArray.addAll(gson.fromJson(prev, groupListType))
                }
                tempArray.remove(data)
                val strList = gson.toJson(tempArray, groupListType)
                sharedPreference.prefs.setString("messageList", strList)
                objects.removeAt(position)
                notifyDataSetChanged()
                mAlertDialog.dismiss()
            }

            mAlertDialog.findViewById<Button>(R.id.Cancel).setOnClickListener {
                mAlertDialog.dismiss()
            }
        }
    }

    override fun getItemCount(): Int {
        return objects.size
    }
}

class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title = itemView.findViewById(R.id.alarmTitle) as TextView
    var body = itemView.findViewById(R.id.alarmBody) as TextView
    var time = itemView.findViewById(R.id.alarmTime) as TextView
    var divline = itemView.findViewById(R.id.alarmDivLine) as ImageView
    var btnDelete = itemView.findViewById(R.id.btnAlarmDelete) as ImageButton
}