import kotlinx.cinterop.*
import lmdb.*
import platform.posix.memcpy

inline fun ByteArray.toMDB_val() : CValue<MDB_val> {
    this.usePinned {
        return cValue<MDB_val> {
            mv_data = it.addressOf(0)
            mv_size = size.toULong()
        }
    }
}

inline fun MDB_val.toByteArray() : ByteArray = ByteArray(this.mv_size.toInt()).apply {
    usePinned {
        memcpy(it.addressOf(0), this@toByteArray.mv_data, this@toByteArray.mv_size)
    }
}
