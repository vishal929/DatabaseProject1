package com.example.cs527_rdsmysql.ui

import ConnectionUtility.RDSConnection
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.cs527_rdsmysql.R

class RDSLoginDialog(private var rds: RDSConnection): DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.rds_login_fragment, container, false)
        val connectButton: Button = view.findViewById(R.id.connectButton)
        connectButton.setOnClickListener {
            val user: EditText = view.findViewById(R.id.rds_username)
            val pass: EditText = view.findViewById(R.id.rds_password)

            rds.user = user.text.toString()
            rds.pass = pass.text.toString()

            AsyncTask.execute {
                rds.connect()
            }
            if (rds.rdsConnection != null) {
                Log.d("connection", "WE GOT A CONNECTION!")
                dismiss()
            } else {
                Log.d("connection", "WE HAVE AN INVALID CONNECTION!")
                Log.d("connection", "db: " + rds.jdbcURL)
                Log.d("connection", "username: " + rds.user)
                Log.d("connection", "password: " + rds.pass)
                val textView: TextView = view.findViewById(R.id.connect_fail_message)
                textView.visibility = View.VISIBLE
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
//        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

}