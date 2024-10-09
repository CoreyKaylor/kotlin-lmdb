import kotlinx.cinterop.memScoped
import lmdb.MDB_val

actual data class Result(actual val resultCode: Int, val key: ByteArray?, val data: ByteArray?)

actual fun Result.toKeyByteArray() = key
actual fun Result.toDataByteArray() : ByteArray? = data
