package ConnectionUtility

import android.util.Log
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient
import software.amazon.awssdk.services.redshiftdata.model.*


class RedshiftConnection(
    var accessKeyID: String, var secretKey:String, var user:String){
    // nullable connection
    var client: RedshiftDataClient = getRedshiftClient()
    // setting default cluster ID for our connection
    var clusterID:String = "redshift-cluster-1"
    // setting a database to query from initially
    var database:String = "dev"


    // getting a client builder object
    fun getRedshiftClient(): RedshiftDataClient{
        // our cluster is on us east 1
        val region:Region = Region.US_EAST_1
        val redshiftDataClient:RedshiftDataClient = RedshiftDataClient.builder()
            .httpClient(UrlConnectionHttpClient.create())
            .region(region)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    accessKeyID,
                    secretKey
                )
            ))
            .build()
        return redshiftDataClient
    }

    // grab a list of databases
    fun getDatabases(dbUser:String, database:String, clusterId:String){
        try {
            val databasesRequest = ListDatabasesRequest.builder()
                .clusterIdentifier(clusterId)
                .dbUser(dbUser)
                .database(database)
                .build()
            val databasesResponse: ListDatabasesResponse =
                client.listDatabases(databasesRequest)
            val databases = databasesResponse.databases()
            for (dbName in databases) {
                println("The database name is : $dbName")
            }
        } catch (e: Exception) {
            Log.d("redshiftConnection", "Something went wrong with grabbing redshift databases:")
            Log.d("redshiftConnection",e.message.toString())
        }
    }

    // running a data query given a valid connection
    fun sendSQLRequest(sqlQuery:String):String{
        try {
            val statementRequest = ExecuteStatementRequest.builder()
                .clusterIdentifier(clusterID)
                .database(database)
                .dbUser(user)
                .sql(sqlQuery)
                .build()
            val response: ExecuteStatementResponse =
                client.executeStatement(statementRequest)
            return response.id()
        } catch (e: Exception) {
            Log.d("RedshiftSqlRequest", "Error with sending request to server:")
            Log.d("RedshiftSqlRequest",e.message.toString())
        }
        // empty id for failed request
        return ""
    }

    // checking the sql request
    fun checkSQLRequest(sqlID:String){
        try {
            val statementRequest = DescribeStatementRequest.builder()
                .id(sqlID)
                .build()

            // Wait until the sql statement processing is finished.
            val finished = false
            var status = ""
            while (!finished) {
                val response: DescribeStatementResponse =
                    client.describeStatement(statementRequest)
                status = response.statusAsString()
                Log.d("RedshiftSQLCheck","Sql request status:")
                Log.d("RedshiftSQLCheck","...$status")
                if (status.compareTo("FINISHED") == 0) {
                    break
                }
                Thread.sleep(1000)
            }
            Log.d("RedshiftSQlCheck","SQL request has been completed!")
        } catch (e: Exception) {
            Log.d("RedshiftSQLCheck","Something went wrong with checking the sql request:")
            Log.d("RedshiftSQLCheck",e.message.toString())
        }
    }

    // grabbing sql request results
    fun grabSQLResult(sqlID:String){
        try {
            val resultRequest = GetStatementResultRequest.builder()
                .id(sqlID)
                .build()
            val response: GetStatementResultResponse =
                client.getStatementResult(resultRequest)

            // Iterate through the List element where each element is a List object.
            val dataList:List<List<Field>> = response.records()

            // Print out the records.
            for (list in dataList) {
                for (myField in list) {
                    val field = myField as Field
                    val value = field.stringValue()
                    if (value != null) {
                        Log.d("SQLRESULT","Got field: $value")
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("SqlResult","Error with grabbing SQL results:")
            Log.d("SqlResult",e.message.toString())
        }
    }
}