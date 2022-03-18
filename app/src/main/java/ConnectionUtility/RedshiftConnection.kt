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

    // grab a list of schemas
    fun getSchemas(): ArrayList<String> {
        val dbUser:String = this.user
        val database:String = this.database
        val clusterId: String = this.clusterID
        try {
            val schemasRequest = ListSchemasRequest.builder()
                .clusterIdentifier(clusterId)
                .dbUser(dbUser)
                .database(database)
                .build()
            val schemasResponse = client.listSchemas(schemasRequest)
            val schemas = ArrayList<String>(schemasResponse.schemas())
            return schemas
        } catch (e: Exception) {
            Log.d("redshiftConnection", "Something went wrong with grabbing redshift schemas:")
            Log.d("redshiftConnection",e.message.toString())
        }
        // return empty array list if failure
        return ArrayList<String>()
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
    fun grabSQLResult(sqlID:String): ResultObject?{
        // grab records as metadata object result set
        var results:ResultObject? = null
        try {
            val resultRequest = GetStatementResultRequest.builder()
                .id(sqlID)
                .build()
            val response: GetStatementResultResponse =
                client.getStatementResult(resultRequest)

            // Iterate through the List element where each element is a List object.
            val dataList:List<List<Field>> = response.records()

            // grabbing column metadata
            val metadata: List<ColumnMetadata> = response.columnMetadata()
            val columnNames:ArrayList<String> = ArrayList<String>()
            // add column metadata for extracting data logic
            for (columnData in metadata){
                columnNames.add(columnData.name())
                Log.d("ColumnMetaDataRedshift",columnData.name())
                Log.d("ColumnMetaDataRedshift",columnData.typeName())
            }
            results = ResultObject()
            results.setMetaData(columnNames)
            for (list in dataList) {
                for (i in list.indices)
                 {
                     //Log.d("at index:",i.toString())
                     val field = list[i] as Field
                     if (results.getTypeFromColNum(i+1)){
                         // we have a string
                         val value = field.stringValue()
                         results.addStrData(i+1,value)
                         //Log.d("SQLRESULT","Got field: $value")
                     } else {
                        // we have an integer
                         val intVal = field.longValue()
                         results.addIntData(i+1,intVal.toInt())
                         //Log.d("SQLResult,","Got field: $intVal")
                     }



                }
            }
        } catch (e: Exception) {
            Log.d("SqlResult","Error with grabbing SQL results:")
            Log.d("SqlResult",e.message.toString())
        }
        return results
    }
}