package ConnectionUtility

import android.util.Log
import software.amazon.awssdk.services.redshiftdata.model.Field
import java.lang.IndexOutOfBoundsException
import java.math.BigDecimal
import java.sql.ResultSet
import java.sql.Time
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// different types we can have from jdbc driver
enum class Type(){
    STRING,
    INT,
    BOOLEAN,
    BIG_DECIMAL,
    SHORT,
    LONG,
    FLOAT,
    DOUBLE,
    BYTE_ARRAY,
    DATE,
    TIME,
    TIMESTAMP
}

// idea is that when we get a result set, we want to put results into a resultObject
// ResultObject will include column headers, and data to display in the app
class ResultObject(){
    // array of string values we might get from a query
    val stringColumns: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>()
    // array of integer values we might get from a query
    val intColumns: ArrayList<ArrayList<Int>> = ArrayList<ArrayList<Int>>()
    // array of boolean values we might get from a query
    val boolColumns: ArrayList<ArrayList<Boolean>> = ArrayList<ArrayList<Boolean>>()
    // array of big decimal values we might get from a query
    val bigDecimalColumns: ArrayList<ArrayList<BigDecimal>> = ArrayList<ArrayList<BigDecimal>>()
    // array of short values we might get from a query
    val shortColumns: ArrayList<ArrayList<Short>> = ArrayList<ArrayList<Short>>()
    // array of long values we might get from a query
    val longColumns: ArrayList<ArrayList<Long>> = ArrayList<ArrayList<Long>>()
    // array of float values we might get from a query
    val floatColumns: ArrayList<ArrayList<Float>> = ArrayList<ArrayList<Float>>()
    // array of double values we might get from a query
    val doubleColumns: ArrayList<ArrayList<Double>> = ArrayList<ArrayList<Double>>()
    // array of Byte_array values we might get from a query
    val byteArrayColumns: ArrayList<ArrayList<ByteArray>> = ArrayList<ArrayList<ByteArray>>()
    // array of date values we might get from a query
    val dateColumns: ArrayList<ArrayList<Date>> = ArrayList<ArrayList<Date>>()
    // array of time values we might get from a query
    val timeColumns: ArrayList<ArrayList<Time>> = ArrayList<ArrayList<Time>>()
    // array of timestamp values we might get from a query
    val timestampColumns: ArrayList<ArrayList<Timestamp>> = ArrayList<ArrayList<Timestamp>>()
    // need to map each column to have uniform output based on column order returned by query
    // colNames in order
    val colNames: ArrayList<String> = ArrayList<String>()
    // type for each name (True for string, false for int)
    //val nameType: ArrayList<Boolean> = ArrayList<Boolean>()
    // type for each name (based on enum value)
    val nameType: ArrayList<Type> = ArrayList<Type>()
    // index of corresponding array
    val indices: ArrayList<Int> = ArrayList<Int>()

    // isStringVal is true for string types in our table, otherwise false (int type)
    /*
    fun metaDataHelper(name:String, isStringVal:Boolean){
        if (isStringVal){
            stringColumns.add(ArrayList<String>())
            colNames.add(name)
            nameType.add(true)
            indices.add(stringColumns.size-1)
        } else{
            intColumns.add(ArrayList<Int>())
            colNames.add(name)
            nameType.add(false)
            indices.add(intColumns.size-1)
        }
    }*/

    fun smartMetaDataHelper(name:String, type:Type){
       when (type){
           Type.INT -> {
               intColumns.add(ArrayList<Int>())
               colNames.add(name)
               nameType.add(Type.INT)
               indices.add(intColumns.size-1)
           }
           Type.STRING -> {
               stringColumns.add(ArrayList<String>())
               colNames.add(name)
               nameType.add(Type.STRING)
               indices.add(stringColumns.size-1)
           }
           Type.BOOLEAN -> {
               boolColumns.add(ArrayList<Boolean>())
               colNames.add(name)
               nameType.add(Type.BOOLEAN)
               indices.add(boolColumns.size-1)
           }
           Type.BIG_DECIMAL -> {
               bigDecimalColumns.add(ArrayList<BigDecimal>())
               colNames.add(name)
               nameType.add(Type.BIG_DECIMAL)
               indices.add(bigDecimalColumns.size-1)
           }
           Type.BYTE_ARRAY -> {
               byteArrayColumns.add(ArrayList<ByteArray>())
               colNames.add(name)
               nameType.add(Type.BYTE_ARRAY)
               indices.add(byteArrayColumns.size-1)
           }
           Type.DATE -> {
               dateColumns.add(ArrayList<Date>())
               colNames.add(name)
               nameType.add(Type.DATE)
               indices.add(dateColumns.size-1)
           }
           Type.DOUBLE -> {
               doubleColumns.add(ArrayList<Double>())
               colNames.add(name)
               nameType.add(Type.DOUBLE)
               indices.add(doubleColumns.size-1)
           }
           Type.FLOAT -> {
               floatColumns.add(ArrayList<Float>())
               colNames.add(name)
               nameType.add(Type.FLOAT)
               indices.add(floatColumns.size-1)
           }
           Type.LONG -> {
               longColumns.add(ArrayList<Long>())
               colNames.add(name)
               nameType.add(Type.LONG)
               indices.add(longColumns.size-1)
           }
           Type.SHORT -> {
               shortColumns.add(ArrayList<Short>())
               colNames.add(name)
               nameType.add(Type.SHORT)
               indices.add(shortColumns.size-1)
           }
           Type.TIME -> {
               timeColumns.add(ArrayList<Time>())
               colNames.add(name)
               nameType.add(Type.TIME)
               indices.add(timeColumns.size-1)
           }
           Type.TIMESTAMP -> {
               timestampColumns.add(ArrayList<Timestamp>())
               colNames.add(name)
               nameType.add(Type.TIMESTAMP)
               indices.add(timestampColumns.size-1)
           }
       }
    }

    // columns are 1 indexed, we expect the column number to be given to this function to be 1 indexed
    fun grabDataRedshift(fieldObject: Field, currColumn:Int){
        val newIndex = currColumn -1
        // grabbing data type from column index
        val type:Type = nameType[newIndex]

        // inserting data based on type
        when (type){
            Type.INT -> {
                if (newIndex < indices.size) {
                    intColumns[indices[newIndex]].add(fieldObject.longValue().toInt())
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding an integer")
                }
            }
            Type.STRING -> {
                if (newIndex < indices.size) {
                    stringColumns[indices[newIndex]].add(fieldObject.stringValue())
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a string")
                }
            }
            Type.BOOLEAN -> {
                if (newIndex < indices.size) {
                    boolColumns[indices[newIndex]].add(fieldObject.booleanValue())
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a boolean")
                }
            }
            Type.BIG_DECIMAL -> {
                if (newIndex < indices.size) {
                    bigDecimalColumns[indices[newIndex]].add(fieldObject.doubleValue().toBigDecimal())
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a big Decimal")
                }
            }
            Type.BYTE_ARRAY -> {
                if (newIndex < indices.size) {
                    byteArrayColumns[indices[newIndex]].add(fieldObject.stringValue().encodeToByteArray())
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a byte array")
                }
            }
            Type.DATE -> {
                if (newIndex < indices.size) {
                    dateColumns[indices[newIndex]].add(SimpleDateFormat().parse(fieldObject.stringValue()))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a date")
                }
            }
            Type.DOUBLE -> {
                if (newIndex < indices.size) {
                    doubleColumns[indices[newIndex]].add(fieldObject.doubleValue())
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a double")
                }
            }
            Type.FLOAT -> {
                if (newIndex < indices.size) {
                    floatColumns[indices[newIndex]].add(fieldObject.doubleValue().toFloat())
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a float")
                }
            }
            Type.LONG -> {
                if (newIndex < indices.size) {
                    longColumns[indices[newIndex]].add(fieldObject.longValue())
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a long")
                }
            }
            Type.SHORT -> {
                if (newIndex < indices.size) {
                    shortColumns[indices[newIndex]].add(fieldObject.longValue().toShort())
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a short")
                }
            }
            Type.TIME -> {
                if (newIndex < indices.size) {
                    timeColumns[indices[newIndex]].add(Time(fieldObject.longValue()))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a time")
                }
            }
            Type.TIMESTAMP -> {
                if (newIndex < indices.size) {
                    timestampColumns[indices[newIndex]].add(Timestamp.valueOf(fieldObject.stringValue()))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a timestamp")
                }
            }
        }
    }

    // columns are 1 indexed, we expect the column number to be given to this function to be 1 indexed
    fun grabDataRDS(rs:ResultSet,currColumn:Int){
        val newIndex = currColumn -1
        // grabbing data type from column index
        val type:Type = nameType[newIndex]

        // inserting data based on type
        when (type){
            Type.INT -> {
                if (newIndex < indices.size) {
                    intColumns[indices[newIndex]].add(rs.getInt(currColumn))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding an integer")
                }
            }
            Type.STRING -> {
                if (newIndex < indices.size) {
                    stringColumns[indices[newIndex]].add(rs.getString(currColumn))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a string")
                }
            }
            Type.BOOLEAN -> {
                if (newIndex < indices.size) {
                    boolColumns[indices[newIndex]].add(rs.getBoolean(currColumn))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a boolean")
                }
            }
            Type.BIG_DECIMAL -> {
                if (newIndex < indices.size) {
                   bigDecimalColumns[indices[newIndex]].add(rs.getBigDecimal(currColumn))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a big Decimal")
                }
            }
            Type.BYTE_ARRAY -> {
                if (newIndex < indices.size) {
                    byteArrayColumns[indices[newIndex]].add(rs.getBytes(currColumn))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a byte array")
                }
            }
            Type.DATE -> {
                if (newIndex < indices.size) {
                    dateColumns[indices[newIndex]].add(rs.getDate(currColumn))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a date")
                }
            }
            Type.DOUBLE -> {
                if (newIndex < indices.size) {
                    doubleColumns[indices[newIndex]].add(rs.getDouble(currColumn))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a double")
                }
            }
            Type.FLOAT -> {
                if (newIndex < indices.size) {
                    floatColumns[indices[newIndex]].add(rs.getFloat(currColumn))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a float")
                }
            }
            Type.LONG -> {
                if (newIndex < indices.size) {
                    longColumns[indices[newIndex]].add(rs.getLong(currColumn))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a long")
                }
            }
            Type.SHORT -> {
                if (newIndex < indices.size) {
                    shortColumns[indices[newIndex]].add(rs.getShort(currColumn))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a short")
                }
            }
            Type.TIME -> {
                if (newIndex < indices.size) {
                    timeColumns[indices[newIndex]].add(rs.getTime(currColumn))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a time")
                }
            }
            Type.TIMESTAMP -> {
                if (newIndex < indices.size) {
                    timestampColumns[indices[newIndex]].add(rs.getTimestamp(currColumn))
                } else {
                    throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding a timestamp")
                }
            }
        }
    }

    fun setMetaData(names:ArrayList<String>, typeNames:ArrayList<String>){
        // idea here is to just set column names, and provide them an ordering
        for (i in 0 until names.size){
            if (typeNames[i]=="INT" || typeNames[i]=="int4" || typeNames[i]=="INTEGER" || typeNames[i]=="int"){
                // should be an integer value
                smartMetaDataHelper(names[i],Type.INT)
            } else if (typeNames[i]=="VARCHAR" || typeNames[i]=="varchar"){
                // string value
                smartMetaDataHelper(names[i],Type.STRING)
            } else if (typeNames[i]=="BOOLEAN" || typeNames[i]=="bool"){
                // boolean value
                smartMetaDataHelper(names[i],Type.BOOLEAN)
            } else if (typeNames[i]=="DECIMAL" || typeNames[i]=="numeric" || typeNames[i] == "NUMERIC"){
                // big decimal value
                smartMetaDataHelper(names[i],Type.BIG_DECIMAL)
            } else if (typeNames[i]=="float4" || typeNames[i]=="FLOAT"){
                // single precision floating point
                smartMetaDataHelper(names[i],Type.FLOAT)
            } else if (typeNames[i]=="float8" || typeNames[i]=="DOUBLE"){
                // double precision floating point
                smartMetaDataHelper(names[i],Type.DOUBLE)
            } else if (typeNames[i]=="date" || typeNames[i]=="DATE"){
                // date value
                smartMetaDataHelper(names[i],Type.DATE)
            } else if (typeNames[i]=="TIME" || typeNames[i]=="time"){
               // time value
                smartMetaDataHelper(names[i],Type.TIME)
            } else if (typeNames[i]=="TIMESTAMP" || typeNames[i]=="timestamp" || typeNames[i]=="DATETIME"){
                // timestamp value
                smartMetaDataHelper(names[i],Type.TIMESTAMP)
            } else if (typeNames[i]=="int2" || typeNames[i]=="SMALLINT"){
                // short value
                smartMetaDataHelper(names[i],Type.SHORT)
            } else if (typeNames[i]=="BINARY" || typeNames[i]=="VARBINARY" || typeNames[i]=="varbinary"){
                // binary array value
                smartMetaDataHelper(names[i],Type.BYTE_ARRAY)
            }
            /*
            if (typeNames[i]=="INT" || typeNames[i]=="int4" || typeNames[i]=="int8" || typeNames[i]=="BIGINT"){
                // should be a integer value
                metaDataHelper(names[i],false)
            } else if (typeNames[i]=="VARCHAR" || typeNames[i]=="varchar"){
                // should be a string value
                metaDataHelper(names[i],true)
            }*/
        }
    }

    // columnIndex here is expected to be 0 indexed
    fun grabStringRepresentation(columnIndex:Int,elementIndex:Int): String{
        // grabbing data type from column index
        val type:Type = nameType[columnIndex]

        // inserting data based on type
        when (type){
            Type.INT -> {
               return intColumns[indices[columnIndex]][elementIndex].toString()
            }
            Type.STRING -> {
                return stringColumns[indices[columnIndex]][elementIndex].toString()
            }
            Type.BOOLEAN -> {
                return boolColumns[indices[columnIndex]][elementIndex].toString()
            }
            Type.BIG_DECIMAL -> {
                return bigDecimalColumns[indices[columnIndex]][elementIndex].toString()
            }
            Type.BYTE_ARRAY -> {
                return byteArrayColumns[indices[columnIndex]][elementIndex].toString()
            }
            Type.DATE -> {
                return dateColumns[indices[columnIndex]][elementIndex].toString()
            }
            Type.DOUBLE -> {
                return doubleColumns[indices[columnIndex]][elementIndex].toString()
            }
            Type.FLOAT -> {
                return floatColumns[indices[columnIndex]][elementIndex].toString()
            }
            Type.LONG -> {
                return longColumns[indices[columnIndex]][elementIndex].toString()
            }
            Type.SHORT -> {
                return shortColumns[indices[columnIndex]][elementIndex].toString()
            }
            Type.TIME -> {
                return timeColumns[indices[columnIndex]][elementIndex].toString()
            }
            Type.TIMESTAMP -> {
                return timestampColumns[indices[columnIndex]][elementIndex].toString()
            }
        }
    }

    /*
    // returns True if a string, false if int
    // if the string is not found in our names, we throw NoSuchFieldException
    fun getTypeFromName(name:String): Boolean{
        for (i in 0..colNames.size){
            val colName:String = colNames[i]
            if (name == colName){
                return nameType[i]
            }
        }
        throw NoSuchFieldException("field name:" + name + " does not exist")
    }

    // returns True if a string, false if int
    // if the given index is out of bounds, we throw NoSuchFieldException
    // columns are 1 indexed in sql, so we decrement the colNum first
    fun getTypeFromColNum(colNum:Int): Boolean{
        val newIndex = colNum-1
        if (newIndex < nameType.size) return nameType[newIndex]
        throw IndexOutOfBoundsException("column number:" + newIndex.toString()+" is out of range when checking column type")
    }

    // adds integer data to a given column
    // throws IndexOutOfBoundsException if the column number is out of range
    // columns are 1 indexed in sql, so we decreement the col num first
    fun addIntData(colNum:Int, value: Int){
        val newIndex = colNum-1
        if (newIndex < indices.size) {
            intColumns[indices[newIndex]].add(value)
        } else {
            throw IndexOutOfBoundsException("column number:" + newIndex.toString()+"  is out of range for adding an integer")
        }
    }

    // adds string data to a given column
    // if the index is out of bounds, we throw IndexOutOfBoundsException
    // columns are 1 indexed in sql, so we decrement the col num first
    fun addStrData(colNum:Int, value: String){
        val newIndex = colNum-1
        if (newIndex < indices.size) {
            stringColumns[indices[newIndex]].add(value)
        } else {
            throw IndexOutOfBoundsException("column number:" + newIndex.toString() + " is out of range for adding a string")
        }
    }*/
}