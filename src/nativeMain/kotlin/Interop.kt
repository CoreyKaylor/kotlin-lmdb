import kotlinx.cinterop.toKString
import lmdb.*

internal fun check(result: Int) {
    if(result == 0) //success
        return
    throwError(result)
}

private fun throwError(result: Int) {
    val error = mdb_strerror(result)!!.toKString()
    throw LmdbException(error)
}

internal fun checkRead(result: Int) {
    if(result == 0 || result == -30798) //success or not found
        return
    throwError(result)
}