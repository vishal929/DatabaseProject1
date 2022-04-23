package com.example.cs527_rdsmysql

import ConnectionUtility.*
import android.graphics.Color
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.cs527_rdsmysql.databinding.ActivityMainBinding
import com.example.cs527_rdsmysql.ui.RDSLoginDialog
import com.example.cs527_rdsmysql.ui.RedshiftLoginDialog


// storing default values for each connection, onSelect for spinner will switch these
var rdsSchema:String = "InstacartReal"
var redshiftSchema:String = "public"

// credentials for redshift
val myAccessKey:String = "AKIAQNBJBONYAZYRUFDM"
val mySecretKey:String = "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"
val myUser:String = "admin"
val redshiftCredentials = RedshiftCredentials(myAccessKey, mySecretKey, myUser, true)

// credentials for rds
val jdbcUrl = "jdbc:mysql://database-1.ctrimk6pz3ln.us-east-1.rds.amazonaws.com:3306/InstacartReal"
val user = "admin"
val pass = "Nosqldatabase527"
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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var rds: RDSConnection = RDSConnection("jdbc:mysql://project1.cabeyzfei4ko.us-east-1.rds.amazonaws.com:3306/Instacart", "", "")
    private var redshift: RedshiftConnection = RedshiftConnection("", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // setting up onclick for clear button (clearing sql query text)
        val clearButton = findViewById<Button>(R.id.clearButton)
        clearButton.setOnClickListener { view ->
            val sqlInText = findViewById<EditText>(R.id.sqlInEditText)
            sqlInText.setText("")
        }

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

        //val navView: BottomNavigationView = binding.navView
        // setting up onSelect for radioGroup to populate spinner
        val radioButtonGroup: RadioGroup = findViewById(R.id.databaseRadioGroup)

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
                val rdsButton: RadioButton = findViewById(R.id.rdsRadioButton)
                if(rdsButton.isChecked && !rdsCredentials.isConnected) {
                    Log.d("test", "rds")
                    RDSLoginDialog(rdsCredentials).show(
                        supportFragmentManager,
                        "LoginFragment"
                    )
                }
                if(rdsCredentials.isConnected) {
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
                if(redshiftCredentials.isConnected) {
                    AsyncTask.execute {
                        // populate spinner with redshift schemas
                        val redshift: RedshiftConnection =
                            RedshiftConnection(myAccessKey, mySecretKey, myUser)
                        redshift.client = redshift.getRedshiftClient()
                        schemas = redshift.getSchemas()
                        // dont need to close connection for redshift, since its request based
                    }
                }
            }
            else ->{
                val spinner:Spinner = findViewById(R.id.dbSpinner)
                val spinnerAdapter:ArrayAdapter<String> = ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, schemas)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = spinnerAdapter
                // nothing selected, so just set spinner to be empty list
                return
            }
        }
        val rdsButton: RadioButton = findViewById(R.id.rdsRadioButton)
        val redshiftButton: RadioButton = findViewById(R.id.redshiftRadioButton)
        if((rdsButton.isChecked && rdsCredentials.isConnected) || (redshiftButton.isChecked && redshiftCredentials.isConnected)) {
            // attaching schemas to spinner
            // checking schemas
            while (schemas.isEmpty()) {
                //just pass until schemas are full (user is not going to be doing anything anyway)
            }
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
    }

    fun queryButtonOnClick(view: View){
        // user tries to run a query without any radio button selected
        // we show an error popup
        val rdsRadio: RadioButton = findViewById(R.id.rdsRadioButton)
        val redshiftRadio: RadioButton = findViewById(R.id.redshiftRadioButton)


        if (!rdsRadio.isChecked && !redshiftRadio.isChecked){
            Toast.makeText(this, "NO DATABASE RADIO BUTTON SELECTED", Toast.LENGTH_SHORT).show()
            return
        }

        val sqlIn: EditText = findViewById(R.id.sqlInEditText)
        val query:String = sqlIn.text.toString().trim()

        // if the query is empty, dont even try any complex logic
        if (query.isEmpty()){
            Toast.makeText(this, "EMPTY QUERY!", Toast.LENGTH_SHORT).show()
            return
        }

        var finalElapsedTime:String = ""
        AsyncTask.execute {
            var result:ResultObject? = null


            // setting a type for result
            sqlIn.movementMethod = ScrollingMovementMethod()
            Log.d("sqlINTextBox", query)
            // run your queries here!
            // grabbing results based on sql text in the sqlInTextView

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

            if (result!=null){
                runOnUiThread {
                    // Stuff that updates the UI
                    //val headerTableRow = findViewById(R.id.table_header_row) as TableRow
                    val tbrow = TableRow(this)
                    // clearing the current table headers
                    //headerTableRow.removeAllViews()
                    val stk = findViewById(R.id.table_main) as TableLayout
                    val headerTable = findViewById<TableLayout>(R.id.headerTable)
                    stk.removeAllViews()
                    headerTable.removeAllViews()


                    // Table Headers
                    result.colNames.forEach { i ->
                        val textView = TextView(this)
                        // layout parameters (should set 0 width, and 1 weight
                        // 0 width, wrap height, and 1 weight
                        val params = TableRow.LayoutParams(0,TableRow.LayoutParams.WRAP_CONTENT,
                            1.0F
                        )
                        textView.layoutParams = params

                        textView.text = i
                        textView.setTextColor(Color.BLACK)
                        textView.gravity = Gravity.CENTER
                        textView.setTypeface(null, Typeface.BOLD)
                        tbrow.addView(textView)
                        //headerTableRow.addView(textView)
                    }
                    stk.addView(tbrow)
                    //headerTable.addView(tbHeader)
                    // Table Data
                    var rowNum: Int = 0
                    // we have as many rows as values in a column
                    var numRows: Int = 0
                    if (result.intColumns.size > 0) numRows = result.intColumns[0].size
                    if (result.stringColumns.size > 0) numRows =
                        Integer.max(numRows, result.stringColumns[0].size)
                    while (rowNum != numRows) {
                        val dataRow = TableRow(this)
                        for (j in 0 until result.indices.size) {
                            val textViewInsert = TextView(this)

                            // layout parameters (should set 0 width, and 1 weight
                            // 0 width, wrap height, and 1 weight
                            val params = TableRow.LayoutParams(0,TableRow.LayoutParams.WRAP_CONTENT,
                                1.0F
                            )
                            textViewInsert.layoutParams = params
                            // insert data into textView
                            textViewInsert.text = result.grabStringRepresentation(j,rowNum)
                            // check if its an int or string type
                            /*
                            if (result.nameType[j]) {
                                // this is a string
                                textViewInsert.text =
                                    (result.stringColumns[result.indices[j]])[rowNum]
                            } else {
                                // must be an int
                                textViewInsert.text =
                                    result.intColumns[result.indices[j]][rowNum].toString()
                            }*/
                            // add textView to the dataRow
                            textViewInsert.setTextColor(Color.BLACK)
                            textViewInsert.gravity = Gravity.CENTER
                            dataRow.addView(textViewInsert)
                        }
                        // add row to the table
                        stk.addView(dataRow)
                        // increment row count
                        rowNum++;
                    }

                    // setting the first row of the data table
                    // as the header row (should not scroll vertically)

                    val headerRow = stk.getChildAt(0)

                    // removing the view from the tablelayout
                    stk.removeViewAt(0)

                    // adding view to the header table(not scrollable vertically)
                    headerTable.addView(headerRow)

                    // we only update layout params on nonempty data
                    if (numRows>0){
                        val layoutParams = stk.getChildAt(0).layoutParams
                        headerTable.getChildAt(0).layoutParams = layoutParams
                    }


                }
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
        } else {
            // then we have some error querying
            // lets just throw up an error dialog
            Toast.makeText(this, "ERROR EXECUTING THE QUERY", Toast.LENGTH_SHORT).show()
        }

    }
}
