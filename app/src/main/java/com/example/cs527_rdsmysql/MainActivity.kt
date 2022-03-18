package com.example.cs527_rdsmysql

import ConnectionUtility.RDSConnection
import ConnectionUtility.RedshiftConnection
import ConnectionUtility.ResultObject
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.cs527_rdsmysql.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*


// storing default values for each connection, onSelect for spinner will switch these
var rdsSchema:String = "Instacart"
var redshiftSchema:String = "public"

// credentials for redshift
val myAccessKey:String = "AKIAXV2NBU57CCNTWLBX"
val mySecretKey:String = "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"
val myUser:String = "dtbs527"

// credentials for rds
val jdbcUrl = "jdbc:mysql://project1.cabeyzfei4ko.us-east-1.rds.amazonaws.com:3306/Instacart"
val user = "dtbs527"
val pass = "Nosqldatabase"

// spinner string arrays
var rdsSpinnerArray:ArrayList<String> = ArrayList<String>()
var redshiftSpinnerArray:ArrayList<String> = ArrayList<String>()

// better generic use of asynchronous task
fun <R> CoroutineScope.executeAsyncTask(
    doInBackground: () -> R,
    onPostExecute: (R) -> Unit
) = launch {
    val result = withContext(Dispatchers.IO) {
        // do async stuff not on main thread
        doInBackground()
    }
    onPostExecute(result)
}

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

        // setting up onSelect for radioGroup to populate spinner
        val radioButtonGroup: RadioGroup = findViewById(R.id.radioButtonGroup)

        radioButtonGroup.setOnCheckedChangeListener { radioGroup, i ->
            radioGroupOnChange(radioGroup,i)
        }

        // setting up onSelect for spinner
        val spinner: Spinner = findViewById(R.id.dbSpinner)
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // dont need to do anything here
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // we do need to change the schema
                val rdsRadioButton: RadioButton = findViewById(R.id.rdsRadioButton)

                // grabbing the selected schema
                val schema:String = parent?.getItemAtPosition(position) as String

                if (rdsRadioButton.isChecked) {
                   rdsSchema = schema
                } else {
                    redshiftSchema = schema
                }
            }
        }

        //val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        //navView.setupWithNavController(navController)
    }

    fun radioGroupOnChange(radioGroup: View, i: Int){
        // get the selected radio button
        var schemas: ArrayList<String> = ArrayList<String>()
        when(i){
            R.id.rdsRadioButton -> {
                AsyncTask.execute {
                    // populate spinner with rds schemas
                    val conn: RDSConnection = RDSConnection(jdbcUrl, user, pass)
                    // try a connection here
                    conn.connect()

                    // grab list of schemas
                    schemas = conn.getSchemas()

                    // closing the connection for rds
                    conn.closeConnection()
                }

            }
            R.id.redshiftRadioButton -> {
                AsyncTask.execute {
                    // populate spinner with redshift schemas
                    val redshift: RedshiftConnection =
                        RedshiftConnection(myAccessKey, mySecretKey, myUser)
                    schemas = redshift.getSchemas()
                    // dont need to close connection for redshift, since its request based
                }
            }
        }
        // attaching schemas to spinner
        // checking schemas
        while (schemas.isEmpty()){
            //just pass until schemas are full (user is not going to be doing anything anyway)
        }
        for (schema in schemas){
            Log.d("schema", schema)
        }
        val spinner:Spinner = findViewById(R.id.dbSpinner)
        val spinnerAdapter:ArrayAdapter<String> = ArrayAdapter<String>(this,
            android.R.layout.simple_spinner_item, schemas)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        // select the previous/ default schema for the user automatically
        for (j in 0 until schemas.size){
            val schema:String = schemas[j]
            if (i == R.id.redshiftRadioButton){
                if (schema == redshiftSchema) spinner.setSelection(j)
            } else if (i==R.id.rdsRadioButton){
                if (schema==rdsSchema) spinner.setSelection(j)
            }
        }
    }

    fun queryButtonOnClick(view: View){
        var finalElapsedTime:String = ""
        AsyncTask.execute {
            var result:ResultObject? = null
            val rdsRadio: RadioButton = findViewById(R.id.rdsRadioButton)
            val redshiftRadio: RadioButton = findViewById(R.id.redshiftRadioButton)
            val sqlIn: EditText = findViewById(R.id.sqlInEditText)
            val query:String = sqlIn.text.toString()
            // setting a type for result
            Log.d("sqlINTextBox", query)
            // run your queries here!
            // grabbing results based on sql text in the sqlInTextView

            // starting the timer
            val t1 = System.currentTimeMillis();
            Log.d("time",t1.toString())
            if (redshiftRadio.isChecked){
                Log.d("redshiftOrRDS","in redshiftLogic")
                // run rds query
                /*REDSHIFT SETUP BELOW*/

                val redshift:RedshiftConnection = RedshiftConnection(myAccessKey,mySecretKey,myUser)
                val sqlID:String = redshift.sendSQLRequest(query)
                redshift.checkSQLRequest(sqlID)
                result  = redshift.grabSQLResult(sqlID)
                if (result == null){
                    // something went wrong, lets set the elapsed time to error
                    finalElapsedTime = "error"
                }
            } else if (rdsRadio.isChecked){
                Log.d("redshiftOrRDS","in rdsLogic")
                // run redshift query
                /*BELOW IS RDS SETUP*/

                val conn: RDSConnection = RDSConnection(jdbcUrl, user, pass)
                // try a connection here
                conn.connect()

                if (conn.rdsConnection != null) {
                    Log.d("connection", "WE GOT A CONNECTION!")
                } else {
                    Log.d("connection", "WE HAVE AN INVALID CONNECTION!")
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
                result = conn.grabData(conn.sqlResultSet!!)
                runOnUiThread {
                    // Stuff that updates the UI
                    val stk = findViewById(R.id.table_main) as TableLayout
                    val tbrow = TableRow(this)
                    stk.removeAllViews()
                    // Table Headers
                    result?.colNames?.forEach { i ->
                        val textView = TextView(this)
                        textView.setText(i)
                        textView.setTextColor(Color.BLACK)
                        textView.gravity = Gravity.CENTER
                        tbrow.addView(textView)
                    }
                    stk.addView(tbrow)
                    // Table Data
                    var i:Int = 0

                    if (result != null) {
                        // For Data without stringColumns
                        if (result.stringColumns.isEmpty()) {
                            result?.intColumns?.forEach { k ->
                                val tbrowIntData = TableRow(this)
                                for (l in k){
                                    val tvInt = TextView(this)
                                    tvInt.setText(l.toString())
                                    tvInt.setTextColor(Color.BLACK)
                                    tvInt.gravity = Gravity.CENTER
                                    tvInt.setBackgroundColor(Color.LTGRAY)
                                    tbrowIntData.addView(tvInt)
                                }
                                stk.addView(tbrowIntData)
                            }
                        } else{
                            result?.intColumns?.first()?.forEach { k ->
                                val tbrowIntData = TableRow(this)
                                val tvInt = TextView(this)
                                val tvStr = TextView(this)
                                while (i < result.stringColumns.first().count()) {
                                    tvInt.setText(k.toString())
                                    tvStr.setText(result.stringColumns.first()[i].toString())
                                    i += 1
                                    break
                                }
                                tvInt.setTextColor(Color.BLACK)
                                tvInt.gravity = Gravity.CENTER
                                tvStr.setTextColor(Color.BLACK)
                                tvStr.gravity = Gravity.CENTER
                                tvInt.setBackgroundColor(Color.LTGRAY)
                                tvStr.setBackgroundColor(Color.GRAY)
                                tbrowIntData.addView(tvInt)
                                tbrowIntData.addView(tvStr)
                                stk.addView(tbrowIntData)
                            }
                        }
                    }

                }
                if (result== null){
                    Log.d("sql","RESULTS IS NULL SOMETHING WENT WRONG!\n");
                    // setting the elapsed time to be some error string
                    finalElapsedTime = "error"
                }
            }
            // ending the timer
            val t2=System.currentTimeMillis();
            Log.d("time",t2.toString())
            if (finalElapsedTime!="error"){
                var timeElapsed = t2-t1
                Log.d("time elapsed ms: ", timeElapsed.toString())
                val min = (timeElapsed/60000)
                timeElapsed -= min*6000
                val sec = (timeElapsed/1000)
                timeElapsed -= sec*1000
                val ms = timeElapsed
                finalElapsedTime = min.toString() + "m " + sec.toString() + "s " + ms.toString() + "ms"
            }


            Log.d("queryOnClick","reached end of onclick")
            /*
            val sqlOut: TextView = findViewById(R.id.sqlOutTextView)
            val outString: String =
                results!!.intColumns.size.toString() + ", " + results.stringColumns.size.toString()
            sqlOut.text = outString
            Log.d("CUSTOM", outString)
            */
        }
        while (finalElapsedTime.isEmpty()){
            // wait for results
        }
        if (finalElapsedTime!="error"){
            val elapsedTime:TextView = findViewById(R.id.TimeElapsedTextViewValue)
            elapsedTime.text = finalElapsedTime
        }

    }
}