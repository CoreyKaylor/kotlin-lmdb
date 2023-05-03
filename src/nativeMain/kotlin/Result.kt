import lmdb.MDB_val

actual data class Result(actual val resultCode: Int, val key: MDB_val, val value: MDB_val)

actual fun Result.toKeyByteArray() : ByteArray = key.toByteArray()
actual fun Result.toDataByteArray() : ByteArray = key.toByteArray()
