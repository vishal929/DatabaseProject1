package com.example.cs527_rdsmysql

import ConnectionUtility.RDSConnection
import ConnectionUtility.ResultObject
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cs527_rdsmysql.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setting button onclick listener
        val sqlButton: Button = findViewById<Button>(R.id.executeQueryButton)
        sqlButton.setOnClickListener { view ->
            queryButtonOnClick(view)
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun queryButtonOnClick(view: View){
        val sqlIn: EditText = findViewById(R.id.sqlInEditText)
        // grabbing results based on sql text in the sqlInTextView
        val jdbcUrl = "URL HERE"
        val user = "USER HERE"
        val pass = "PASS HERE"
        val conn: RDSConnection = RDSConnection(jdbcUrl, user, pass)
        // try a connection here
        conn.connect()
        // if successful, we move onto sql query
        conn.executeSQL(sqlIn.text.toString())
        // if we executed the query successfully, we move onto results gathering
        // CHECK FOR NULL HERE
        val results: ResultObject? = conn.grabData(conn.sqlResultSet!!)
        // IF results is null, something went wrong with grabbing data
        // if we reached here we successfully grabbed our data
        val sqlOut : TextView = findViewById(R.id.sqlOutTextView)
        val outString: String = results!!.intColumns.size.toString() + ", " + results.stringColumns.size.toString()
        sqlOut.text = outString
        Log.d("CUSTOM",outString)
    }
}