package ConnectionUtility
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLTimeoutException
import java.sql.SQLException

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

    // executing a query and buffering results
    fun executeSQL(sql: String){

    }
}