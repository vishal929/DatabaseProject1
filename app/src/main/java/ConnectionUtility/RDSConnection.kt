package ConnectionUtility
import java.sql.Connection
import java.sql.DriverManager
// exceptions
import java.sql.SQLException
import java.sql.SQLTimeoutException
// prepared statement is the wrapper for sql text support
import java.sql.PreparedStatement
// result set is the object that holds raw sql output as a cursor iteration
import java.sql.ResultSet
// metadata for result set
import java.sql.ResultSetMetaData
// need the result object for packing our sql queries
import ConnectionUtility.ResultObject

// class that manages connection between the application and our rds server
class RDSConnection (jdbc: String, username: String, password: String){

    // implicit setters and getters in kotlin
    var jdbcURL: String = jdbc
    var user: String = username
    var pass: String = password
    // nullable connection, since if a connection was invalid, we assign it null
    var rdsConnection: Connection? = null

    // connecting to our RDS database via jdbc url, could be connection exception
    fun connect(): Int {
        var driverConnection : Connection? = null
        try {
            driverConnection = DriverManager.getConnection(jdbcURL,user,pass)
        } catch(e: SQLException){
            // error with access to database or url is null
            return 1
        } catch(e: SQLTimeoutException){
            // connection timed out for the driver
            return 2
        }
        // setting our connection to be the successful one
        rdsConnection = driverConnection
        return 0

    }

    // executing a query
    fun executeSQL(sql: String): ResultSet?{
        // checking if the connection is still valid
        // waiting 5 seconds maximum for a timeout
        // double exclamation is to assert that the connection is not null at this point
        // since there arent multiple threads accessing the connection
        if (rdsConnection == null || !rdsConnection!!.isValid(5)){
            rdsConnection = null
            return null
        }
        // we have some connection here, lets try accessing the results
        val query: PreparedStatement = rdsConnection!!.prepareStatement(sql)

        val results: ResultSet? = try {
            query.executeQuery()
        } catch(e: Exception) {
            // some error with the sql query
            null
        }

        // returning the result set
        return results

    }

    // grabbing all the data from a result set (no need to buffer since our data is not large in this example)
    // returns a type of ResultObject (contains data to display to the user)
    fun grabData(rs: ResultSet): ResultObject?{
        val result:ResultObject = ResultObject()
        try {
            // grabbing metadata
            val metaData: ResultSetMetaData = rs.metaData
            val numColumns = metaData.columnCount
            val colNames: ArrayList<String> = ArrayList<String>()
            for (i in 0..numColumns){
                colNames.add(metaData.getColumnName(i))
            }
            // initializing the resultObject based on metadata from ResultSet (column names, etc.)
            result.setMetaData(colNames)
            while (rs.next()) {
                // extracting data from each column using the index
                for (i in 0..numColumns){
                    if (result.getTypeFromColNum(i)){
                        // we have a string
                        result.addStrData(i,rs.getString(i))
                    } else{
                        // we have an int
                        result.addIntData(i,rs.getInt(i))
                    }
                }
            }
        } catch (e:Exception){
            // something went wrong with the results
            return null
        }
        return result
    }




}