import kotlinx.cinterop.*
import lmdb.*

inline fun <T> MemScope.withMDB_val(data:ByteArray, crossinline block: (MDB_val) -> T) : T {
    data.usePinned {
        val mdbVal = alloc<MDB_val> {
            mv_data = it.addressOf(0)
            mv_size = data.size.convert()
        }
        return block(mdbVal)
    }
}

inline fun <T> MemScope.withMDB_val(key: ByteArray, data: ByteArray, crossinline block: (key: MDB_val, data: MDB_val) -> T) : T {
    return withMDB_val(key) { mdbKey ->
        withMDB_val(data) { mdbData ->
            block(mdbKey, mdbData)
        }
    }
}

fun MDB_val.toByteArray() : ByteArray? {
    val size = this.mv_size.toInt()
    return this.mv_data?.readBytes(size)
}
