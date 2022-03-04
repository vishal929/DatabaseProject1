package com.example.cs527_rdsmysql

import ConnectionUtility.RDSConnection
import ConnectionUtility.RedshiftConnection
import ConnectionUtility.ResultObject
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cs527_rdsmysql.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement


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

        AsyncTask.execute {
            val sqlIn: EditText = findViewById(R.id.sqlInEditText)
            Log.d("sqlINTextBox", sqlIn.text.toString())
            // run your queries here!
            // grabbing results based on sql text in the sqlInTextView
            /*BELOW IS RDS SETUP*/
            /*
            val jdbcUrl = "jdbc:mysql://project1.cabeyzfei4ko.us-east-1.rds.amazonaws.com:3306/Instacart"
            val user = "dtbs527"
            val pass = "Nosqldatabase"
            val conn: RDSConnection = RDSConnection(jdbcUrl, user, pass)
            // try a connection here
            conn.connect(true)

            if (conn.rdsConnection != null) {
                Log.d("connection", "WE GOT A CONNECTION!")
            } else {
                Log.d("connection", "WE HAVE AN INVALID CONNECTION!")
            }
            val schemaNames:ArrayList<String> = conn.getSchemas()
            for (schema in schemaNames){
                Log.d("schemaPrinting",schema)
            }
            var currentSchema:String = conn.getCurrentSchema()
            Log.d("currentSchemaCheck",currentSchema)

            // switching schema for fun
            conn.selectSchema("InstacartCopy")

            currentSchema = conn.getCurrentSchema()
            Log.d("currentSchemaCheck",currentSchema)

            // if successful, we move onto sql query
            conn.executeSQL(sqlIn.text.toString())

             */
            /*REDSHIFT SETUP BELOW*/
            val myAccessKey:String = ""
            val mySecretKey:String = ""
            val myUser:String = "dtbs527"
            val redshift:RedshiftConnection = RedshiftConnection(myAccessKey,mySecretKey,myUser)
            val sqlID:String = redshift.sendSQLRequest("SELECT * FROM aisles")
            redshift.checkSQLRequest(sqlID)
            redshift.grabSQLResult(sqlID)

            // if we executed the query successfully, we move onto results gathering
            // CHECK FOR NULL HERE
            /*
            val results: ResultObject? = conn.grabData(conn.sqlResultSet!!)
            if (results== null){
                Log.d("sql","RESULTS IS NULL SOMETHING WENT WRONG!\n");
            }
            */
            // IF results is null, something went wrong with grabbing data
            // if we reached here we successfully grabbed our data
            /*
            val sqlOut: TextView = findViewById(R.id.sqlOutTextView)
            val outString: String =
                results!!.intColumns.size.toString() + ", " + results.stringColumns.size.toString()
            sqlOut.text = outString
            Log.d("CUSTOM", outString)
            */
        }
    }
}