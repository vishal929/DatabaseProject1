package com.example.cs527_rdsmysql

<<<<<<< Updated upstream
import ConnectionUtility.RDSConnection
import ConnectionUtility.RedshiftConnection
import ConnectionUtility.ResultObject
=======
//import com.example.cs527_rdsmysql.ui.RedshiftLoginDialog
import ConnectionUtility.*
import android.graphics.Color
import android.graphics.Typeface
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement


=======


// storing default values for each connection, onSelect for spinner will switch these
var rdsSchema:String = "Instacart"
var redshiftSchema:String = "public"

// credentials for redshift
val myAccessKey:String = "AKIAXV2NBU57CCNTWLBX"
val mySecretKey:String = "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"
val myUser:String = "dtbs527"
val redshiftCredentials = RedshiftCredentials(myAccessKey, mySecretKey, myUser, true)

// credentials for rds
val jdbcUrl = "jdbc:mysql://project1.cabeyzfei4ko.us-east-1.rds.amazonaws.com:3306/Instacart"
val user = "dtbs527"
val pass = "Nosqldatabase"
val rdsCredentials = RDSCredentials(jdbcUrl, user, pass, true)

//// credentials for redshift
//var myAccessKey:String = ""
//var mySecretKey:String = ""
//var myUser:String = ""
//
//// credentials for rds
//var jdbcUrl = "jdbc:mysql://project1.cabeyzfei4ko.us-east-1.rds.amazonaws.com:3306/Instacart"
//var user = ""
//var pass = ""

>>>>>>> Stashed changes
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

//    private var rdsConnection: Boolean = false
//    private var redshiftConnection: Boolean = false

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
            rdsCredentials.clear()
            redshiftCredentials.clear()
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

<<<<<<< Updated upstream
    fun queryButtonOnClick(view: View) {
=======
    fun radioGroupOnChange(radioGroup: View, i: Int){
        val radioButtonGroup: RadioGroup = findViewById(R.id.databaseRadioGroup)
        // get the selected radio button
        var schemas: ArrayList<String> = ArrayList<String>()
        when(i){
            R.id.rdsRadioButton -> {
                val rdsButton: RadioButton = findViewById(R.id.rdsRadioButton)
                if(rdsButton.isChecked && !rdsCredentials.isConnected) {
                    Log.d("test", "rds")
                    RDSLoginDialog(rdsCredentials).show(
                        supportFragmentManager,
                        "LoginFragment"
                    )
                }
//                AsyncTask.execute {
//                    // populate spinner with rds schemas
//                    val conn: RDSConnection = RDSConnection(jdbcUrl, user, pass)
//                    // try a connection here
//                    conn.connect()
//
//                    // grab list of schemas
//                    schemas = conn.getSchemas()
//
//                    // closing the connection for rds
//                    conn.closeConnection()
//                }

            }
            R.id.redshiftRadioButton -> {
                val redshiftButton: RadioButton = findViewById(R.id.redshiftRadioButton)
                if(redshiftButton.isChecked && !redshiftCredentials.isConnected) {
                    Log.d("test", "rds")
                    RedshiftLoginDialog(redshiftCredentials).show(
                        supportFragmentManager,
                        "LoginFragment"
                    )
                }
//                Log.d("test", "redshift")
//                AsyncTask.execute {
//                    // populate spinner with redshift schemas
//                    val redshift: RedshiftConnection =
//                        RedshiftConnection(myAccessKey, mySecretKey, myUser)
//                    redshift.client = redshift.getRedshiftClient()
//                    schemas = redshift.getSchemas()
//                    // dont need to close connection for redshift, since its request based
//                }
            }
            else ->{
                Log.d("test", "spinner")
                val spinner:Spinner = findViewById(R.id.dbSpinner)
                val spinnerAdapter:ArrayAdapter<String> = ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, schemas)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = spinnerAdapter
                // nothing selected, so just set spinner to be empty list
                return
            }
        }
        // attaching schemas to spinner
        // checking schemas
//        while (schemas.isEmpty()) {
//            //just pass until schemas are full (user is not going to be doing anything anyway)
//        }
        for (schema in schemas) {
            Log.d("schema", schema)
        }
        val spinner: Spinner = findViewById(R.id.dbSpinner)
        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item, schemas
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        // select the previous/ default schema for the user automatically
        for (j in 0 until schemas.size) {
            val schema: String = schemas[j]
            if (i == R.id.redshiftRadioButton) {
                if (schema == redshiftSchema) spinner.setSelection(j)
            } else if (i == R.id.rdsRadioButton) {
                if (schema == rdsSchema) spinner.setSelection(j)
            }
        }
    }
>>>>>>> Stashed changes

        AsyncTask.execute {
            val sqlIn: EditText = findViewById(R.id.sqlInEditText)
            Log.d("sqlINTextBox", sqlIn.text.toString())
            // run your queries here!
            // grabbing results based on sql text in the sqlInTextView
<<<<<<< Updated upstream
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
=======

            // starting the timer
            val t1 = System.currentTimeMillis();
            Log.d("time",t1.toString())
            if (redshiftRadio.isChecked && redshiftCredentials.isConnected){
                Log.d("redshiftOrRDS","in redshiftLogic")
                // run rds query
                /*REDSHIFT SETUP BELOW*/

                val myAccessKey = redshiftCredentials.accessKey
                val mySecretKey = redshiftCredentials.secretKey
                val myUser = redshiftCredentials.user
                val redshift:RedshiftConnection = RedshiftConnection(myAccessKey,mySecretKey,myUser)
                // placeholder for valid connection
                redshift.client = redshift.getRedshiftClient()
                redshift.redshiftConnection=true
                val sqlID:String = redshift.sendSQLRequest(query)
                redshift.checkSQLRequest(sqlID)
                result  = redshift.grabSQLResult(sqlID)
                if (result == null){
                    // something went wrong, lets set the elapsed time to error
                    finalElapsedTime = "error"
                }
                Log.d("connection", "username: $myUser")
                Log.d("connection", "access key: $myAccessKey")
                Log.d("connection", "secret key: $mySecretKey")
            } else if (rdsRadio.isChecked && rdsCredentials.isConnected){
                Log.d("redshiftOrRDS","in rdsLogic")
                // run redshift query
                /*BELOW IS RDS SETUP*/

                val jdbcUrl = rdsCredentials.jdbcUrl
                val user = rdsCredentials.user
                val pass = rdsCredentials.pass
                val conn: RDSConnection = RDSConnection(jdbcUrl, user, pass)
                // try a connection here
                conn.connect()

                if (conn.rdsConnection != null) {
                    Log.d("connection", "WE GOT A CONNECTION!")
                } else {
                    Log.d("connection", "WE HAVE AN INVALID CONNECTION!")
                    // returning since invalid
                    return@execute
                }

                // picking the selected schema
                conn.selectSchema(rdsSchema)
                /*
                val schemaNames:ArrayList<String> = conn.getSchemas()
                for (schema in schemaNames){
                    Log.d("schemaPrinting",schema)
                }
                */
                val currentSchema:String = conn.getCurrentSchema()
                Log.d("currentSchemaCheck",currentSchema)

                // switching schema for fun
                //conn.selectSchema("InstacartCopy")

                //currentSchema = conn.getCurrentSchema()
                //Log.d("currentSchemaCheck",currentSchema)

                // if successful, we move onto sql query
                conn.executeSQL(query)

                // we move onto grabbing results
                if (conn.sqlResultSet != null){
                    result = conn.grabData(conn.sqlResultSet!!)
                }

                if (result== null){
                    Log.d("sql","RESULTS IS NULL SOMETHING WENT WRONG!\n");
                    // setting the elapsed time to be some error string
                    finalElapsedTime = "error"
                }
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
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
=======
//    fun onRadioButtonClicked(view: View) {
//        if (view is RadioButton) {
//            // Is the button now checked?
//            val checked = view.isChecked
//
//            // Check which radio button was clicked
//            when (view.getId()) {
//                R.id.rdsRadioButton ->
//                    if (checked) {
//                        if (rds.rdsConnection == null) {
//                            RDSLoginDialog(rds).show(supportFragmentManager, "LoginFragment")
//                        }
//                    }
//                R.id.redshiftRadioButton ->
//                    if (checked) {
//                        if (!redshift.redshiftConnection) {
//                            //RedshiftLoginDialog(redshift).show(supportFragmentManager, "LoginFragment")
//                            val test = 1
//                        }
//                    }
//            }
//        }
//    }
>>>>>>> Stashed changes
}
