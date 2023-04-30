package ConnectionUtility

class RDSCredentials(val jdbcUrl: String, var user:String, var pass:String, var isConnected: Boolean){
    fun clear() {
        user = ""
        pass = ""
        isConnected = false
    }

    fun set(user: String, pass: String) {
        this.user = user
        this.pass = pass
    }
}