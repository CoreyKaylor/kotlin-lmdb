import kotlinx.cinterop.toKString
import lmdb.*
actual fun native_mdb_strerror(result: Int) : String {
    return mdb_strerror(result)!!.toKString()
}