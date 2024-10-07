import lmdb.MDB_val

actual data class Result(actual val resultCode: Int, val key: MDB_val, val data: MDB_val)

actual fun Result.toKeyByteArray() : ByteArray = key.toByteArray()
actual fun Result.toDataByteArray() : ByteArray = data.toByteArray()
