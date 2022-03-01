package ConnectionUtility

import android.util.Log
import java.lang.IndexOutOfBoundsException

// idea is that when we get a result set, we want to put results into a resultObject
// ResultObject will include column headers, and data to display in the app
class ResultObject(){
    // array of string values we might get from a query
    val stringColumns: ArrayList<ArrayList<String>> = ArrayList<ArrayList<String>>()
    // array of integer values we might get from a query
    val intColumns: ArrayList<ArrayList<Int>> = ArrayList<ArrayList<Int>>()
    // need to map each column to have uniform output based on column order returned by query
    // colNames in order
    val colNames: ArrayList<String> = ArrayList<String>()
    // type for each name (True for string, false for int)
    val nameType: ArrayList<Boolean> = ArrayList<Boolean>()
    // index of corresponding array
    val indices: ArrayList<Int> = ArrayList<Int>()

    // isStringVal is true for string types in our table, otherwise false (int type)
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
    }

    fun setMetaData(names:ArrayList<String>){
        // idea here is to just set column names, and provide them an ordering
        for (name in names){
            when(name){
                "department_id" -> {
                    metaDataHelper("department_id", false)
                }
                "department" -> {
                    metaDataHelper("department", true)
                }
                "aisle_id" -> {
                    metaDataHelper("aisle_id",false)
                }
                "aisle" -> {
                    metaDataHelper("aisle", true)
                }
                "product_id" -> {
                    metaDataHelper("product_id", false)
                }
                "product_name" -> {
                    metaDataHelper("product_name", true)
                }
                "order_id" -> {
                    metaDataHelper("order_id", false)
                }
                "user_id" -> {
                    metaDataHelper("user_id", false)
                }
                "order_number" -> {
                    metaDataHelper("order_number", false)
                }
                "order_dow" -> {
                    metaDataHelper("order_dow",false)
                }
                "order_hour_of_day" -> {
                    metaDataHelper("order_hour_of_day", false)
                }
                "days_since_prior_order" -> {
                    metaDataHelper("days_since_prior_order", false)
                }
                "add_to_cart_order" -> {
                    metaDataHelper("add_to_cart_order", false)
                }
                "reordered" -> {
                    metaDataHelper("reordered",false)
                }
                else -> {
                    Log.d("ResultObjectDebug", "Given element not found!")
                }
            }
        }
    }

    // returns True if a string, false if int
    // if the string is not found in our names, we throw NoSuchFieldException
    fun getTypeFromName(name:String): Boolean{
        for (i in 0..colNames.size){
            val colName:String = colNames[i]
            if (name == colName){
                return nameType[i]
            }
        }
        throw NoSuchFieldException("field name does not exist")
    }

   // returns True if a string, false if int
    // if the given index is out of bounds, we throw NoSuchFieldException
    // columns are 1 indexed in sql, so we decrement the colNum first
    fun getTypeFromColNum(colNum:Int): Boolean{
       val newIndex = colNum-1
        if (newIndex < nameType.size) return nameType[newIndex]
        throw IndexOutOfBoundsException("column number is out of range when checking column type")
    }

   // adds integer data to a given column
    // throws IndexOutOfBoundsException if the column number is out of range
    // columns are 1 indexed in sql, so we decreement the col num first
    fun addIntData(colNum:Int, value: Int){
       val newIndex = colNum-1
        if (newIndex < indices.size) {
            intColumns[indices[newIndex]].add(value)
        } else {
            throw IndexOutOfBoundsException("column number is out of range for adding an integer")
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
           throw IndexOutOfBoundsException("column number is out of range for adding a string")
       }
    }
}