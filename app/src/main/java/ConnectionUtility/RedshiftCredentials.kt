package ConnectionUtility

data class RedshiftCredentials(var accessKey: String, var secretKey: String, var user: String, var isConnected: Boolean) {
    fun clear() {
        accessKey = ""
        secretKey = ""
        user = ""
        isConnected = false
    }

    fun set(accessKey: String, secretKey: String, user: String) {
        this.accessKey = accessKey
        this.secretKey = secretKey
        this.user = user
    }
}
