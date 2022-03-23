package com.example.cs527_rdsmysql.ui

import ConnectionUtility.RedshiftConnection
<<<<<<< Updated upstream
=======
import ConnectionUtility.RedshiftCredentials
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
class RedshiftLoginDialog(private var redshift: RedshiftConnection): DialogFragment() {
=======
class RedshiftLoginDialog(private var credentials: RedshiftCredentials): DialogFragment() {
>>>>>>> Stashed changes

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.redshift_login_fragment, container, false)
        val connectButton: Button = view.findViewById(R.id.connectButton)
        connectButton.setOnClickListener {
<<<<<<< Updated upstream
            val user: EditText = view.findViewById(R.id.redshift_username)
            val accessKey: EditText = view.findViewById(R.id.redshift_access_key)
            val secretKey: EditText = view.findViewById(R.id.redshift_secret_key)

            redshift.user = user.text.toString()
            redshift.accessKeyID = accessKey.text.toString()
            redshift.secretKey = secretKey.text.toString()

//            redshift.accessKeyID = "AKIAXV2NBU57CCNTWLBX"
//            redshift.secretKey = "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"
//            redshift.user = "dtbs527"

            if (redshift.testConnection()) {
                Log.d("connection", "WE GOT A CONNECTION!")
                Log.d("connection", "username: " + redshift.user)
                Log.d("connection", "access key: " + redshift.accessKeyID)
                Log.d("connection", "secret key: " + redshift.secretKey)
                dismiss()
            } else {
                Log.d("connection", "WE HAVE AN INVALID CONNECTION!")
                Log.d("connection", "username: " + redshift.user)
                Log.d("connection", "access key: " + redshift.accessKeyID)
                Log.d("connection", "secret key: " + redshift.secretKey)
=======
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
            AsyncTask.execute {
                // populate spinner with redshift schemas
                val redshift: RedshiftConnection =
                    RedshiftConnection(accessKey, secretKey, user)
                redshift.client = redshift.getRedshiftClient()
                schemas = redshift.getSchemas()
            }
            while (schemas == null) {}
            if (schemas!!.isNotEmpty()) {
                Log.d("connection", "WE GOT A CONNECTION!")
                Log.d("connection", schemas.toString())
                credentials.isConnected = true
                dismiss()
            } else {
                Log.d("connection", "WE HAVE AN INVALID CONNECTION!")
                Log.d("connection", "username: $user")
                Log.d("connection", "access key: $accessKey")
                Log.d("connection", "secret key: $secretKey")
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream

}
=======
}
>>>>>>> Stashed changes
