import kotlinx.cinterop.memScoped
import lmdb.MDB_val

actual data class Result(actual val resultCode: Int, val key: MDB_val, val data: MDB_val)

actual fun Result.toKeyByteArray() : ByteArray? = memScoped {
    key.toByteArray()
}
actual fun Result.toDataByteArray() : ByteArray? = memScoped {
    data.toByteArray()
}
