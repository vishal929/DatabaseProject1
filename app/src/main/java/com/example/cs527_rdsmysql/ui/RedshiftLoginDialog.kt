package com.example.cs527_rdsmysql.ui

import ConnectionUtility.RedshiftConnection
import ConnectionUtility.RedshiftCredentials
import ConnectionUtility.ResultObject
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.cs527_rdsmysql.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class RedshiftLoginDialog(private var credentials: RedshiftCredentials): DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.redshift_login_fragment, container, false)
        val connectButton: Button = view.findViewById(R.id.connectButton)
        connectButton.setOnClickListener {
            val userID: EditText = view.findViewById(R.id.redshift_username)
            val accessKeyID: EditText = view.findViewById(R.id.redshift_access_key)
            val secretKeyID: EditText = view.findViewById(R.id.redshift_secret_key)

            val user = userID.text.toString()
            var accessKey = accessKeyID.text.toString()
            var secretKey = secretKeyID.text.toString()

            accessKey = "AKIAXV2NBU57CCNTWLBX"
            secretKey = "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"
//            redshift.user = "dtbs527"

            credentials.clear()
            credentials.set(accessKey, secretKey, user)

            var schemas: ArrayList<String>? = null
            var result:ResultObject? = null
            AsyncTask.execute {
                // populate spinner with redshift schemas
                val redshift: RedshiftConnection =
                    RedshiftConnection(accessKey, secretKey, user)
                redshift.client = redshift.getRedshiftClient()
                redshift.redshiftConnection=true
                val sqlID:String = redshift.sendSQLRequest("Select * From AISLES;")
                redshift.checkSQLRequest(sqlID)
                result = redshift.grabSQLResult(sqlID)
            }
            runBlocking {
                repeat(5) {
                    if (result == null) {
                        delay(1000L)
                    }
                }
            }
            if (result != null) {
                Log.d("connection", "WE GOT A CONNECTION!")
                Log.d("connection", schemas.toString())
                credentials.isConnected = true
                dismiss()
            } else {
                Log.d("connection", "WE HAVE AN INVALID CONNECTION!")
                Log.d("connection", "username: $user")
                Log.d("connection", "access key: $accessKey")
                Log.d("connection", "secret key: $secretKey")
                val textView: TextView = view.findViewById(R.id.redshift_connect_fail_message)
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
