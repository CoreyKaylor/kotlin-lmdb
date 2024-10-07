import kotlinx.cinterop.*
import lmdb.*
import platform.posix.memcpy

inline fun <T> MemScope.toPinnedMDB_val(data: ByteArray, crossinline block: (MDB_val) -> T) : T {
    data.usePinned {
        memScoped {
            val mdbVal = cValue<MDB_val> {
                mv_data = it.addressOf(0)
                mv_size = data.size.convert()
            }
            return block(mdbVal.ptr.pointed)
        }
    }
}

inline fun <T> MemScope.toPinnedMDB_val(key: ByteArray, data: ByteArray, crossinline block: (key: MDB_val, data: MDB_val) -> T) : T {
    return this.toPinnedMDB_val(key) { mdbKey ->
        this.toPinnedMDB_val(data) { mdbData ->
            block(mdbKey, mdbData)
        }
    }
}

inline fun MDB_val.toByteArray() : ByteArray = ByteArray(this.mv_size.convert()).apply {
    this.usePinned {
        println("Array $size")
        memcpy(it.addressOf(0), this@toByteArray.mv_data, this@toByteArray.mv_size)
    }
}
