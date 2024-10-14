import kotlinx.cinterop.*
import kotlin.native.ref.createCleaner
import lmdb.MDB_val
import kotlin.experimental.ExperimentalNativeApi

actual class Val(val mdbVal: MDB_val, private val arena: Arena) {
    @OptIn(ExperimentalNativeApi::class)
    private val cleaner = createCleaner(this, Val::clean)

    fun clean() {
        arena.clear()
    }

    actual fun toByteArray() : ByteArray? {
        val size = mdbVal.mv_size.toInt()
        return this.mdbVal.mv_data?.readBytes(size)
    }

    companion object {
        fun output() : Val {
            val arena = Arena()
            val mdbVal = arena.alloc<MDB_val>()
            return Val(mdbVal, arena)
        }

        fun input(byteArray: ByteArray): Val = memScoped {
            val arena = Arena()
            return byteArray.usePinned {
                val mdbVal = arena.alloc<MDB_val> {
                    mv_data = it.addressOf(0)
                    mv_size = byteArray.size.convert()
                }
                Val(mdbVal, arena)
            }
        }
    }
}

actual fun ByteArray.toVal() : Val {
    return Val.input(this)
}