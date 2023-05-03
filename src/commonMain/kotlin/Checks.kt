internal fun check(result: Int) : Int {
    if(result == 0) //success
        return result
    throw getException(result)
}

private fun getException(result: Int) : LmdbException {
    val error = native_mdb_strerror(result)
    return LmdbException(error)
}

internal fun checkRead(result: Int) : Int {
    if(result == 0 || result == -30798) //success or not found
        return result
    throw getException(result)
}

expect fun native_mdb_strerror(result: Int) : String