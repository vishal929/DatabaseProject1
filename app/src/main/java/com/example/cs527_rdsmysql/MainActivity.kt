package com.example.cs527_rdsmysql

import ConnectionUtility.RDSConnection
import ConnectionUtility.RedshiftConnection
import ConnectionUtility.ResultObject
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cs527_rdsmysql.databinding.ActivityMainBinding
import com.example.cs527_rdsmysql.ui.RDSLoginDialog
import com.example.cs527_rdsmysql.ui.RedshiftLoginDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var rds: RDSConnection = RDSConnection("jdbc:mysql://project1.cabeyzfei4ko.us-east-1.rds.amazonaws.com:3306/Instacart", "", "")
    private var redshift: RedshiftConnection = RedshiftConnection("", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setting button onclick listener
        val sqlButton: Button = findViewById<Button>(R.id.executeQueryButton)
        sqlButton.setOnClickListener { view ->
            queryButtonOnClick(view)
        }

        val resetButton: Button = findViewById(R.id.resetButton)
        resetButton.setOnClickListener {
            rds.user = ""
            rds.pass = ""
            rds.rdsConnection = null
            redshift.user = ""
            redshift.accessKeyID = ""
            redshift.secretKey = ""
            redshift.redshiftConnection = false
            redshift.client = null
            val dbRadioGroup: RadioGroup = findViewById(R.id.databaseRadioGroup)
            dbRadioGroup.clearCheck()
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

    fun queryButtonOnClick(view: View) {

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
            val myAccessKey: String = "AKIAXV2NBU57CCNTWLBX"
            val mySecretKey: String = "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"
            val myUser: String = "dtbs527"
            val redshift: RedshiftConnection = RedshiftConnection(myAccessKey, mySecretKey, myUser)
            val sqlID: String = redshift.sendSQLRequest("SELECT * FROM aisles")
            redshift.checkSQLRequest(sqlID)
            val result: ResultObject? = redshift.grabSQLResult(sqlID)

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

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.rdsRadioButton ->
                    if (checked) {
                        if (rds.rdsConnection == null) {
                            RDSLoginDialog(rds).show(supportFragmentManager, "LoginFragment")
                        }
                    }
                R.id.redshiftRadioButton ->
                    if (checked) {
                        if (!redshift.redshiftConnection) {
                            RedshiftLoginDialog(redshift).show(supportFragmentManager, "LoginFragment")
                        }
                    }
            }
        }
    }
}
