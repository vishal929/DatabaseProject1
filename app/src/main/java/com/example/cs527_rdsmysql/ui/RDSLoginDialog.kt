package com.example.cs527_rdsmysql.ui

import ConnectionUtility.RDSConnection
import ConnectionUtility.RDSCredentials
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.cs527_rdsmysql.R
import com.example.cs527_rdsmysql.rdsCredentials
import kotlinx.coroutines.*


class RDSLoginDialog(private val credentials: RDSCredentials): DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.rds_login_fragment, container, false)
        val connectButton: Button = view.findViewById(R.id.connectButton)
        connectButton.setOnClickListener {
            val userID: EditText = view.findViewById(R.id.rds_username)
            val passID: EditText = view.findViewById(R.id.rds_password)

            val user = userID.text.toString()
            val pass = passID.text.toString()
            credentials.set(user, pass)

            // populate spinner with rds schemas
            val conn = RDSConnection(credentials.jdbcUrl, user, pass)
            // try a connection here
            var isConnected = -1
            AsyncTask.execute {isConnected = conn.connect()}
            runBlocking {
                repeat(10) {
                    if (isConnected < 0 || isConnected > 2) {
                        delay(1000L)
                    }
                }
            }

            if (isConnected == 0) {
                Log.d("connection", "WE GOT A CONNECTION!")
                credentials.isConnected = true
                dismiss()
            } else {
                Log.d("connection", "WE HAVE AN INVALID CONNECTION!")
                Log.d("connection", "db: " + credentials.jdbcUrl)
                Log.d("connection", "username: $user")
                Log.d("connection", "password: $pass")
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
