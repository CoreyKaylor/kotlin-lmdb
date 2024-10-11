import kotlinx.cinterop.*
import lmdb.*

actual class Cursor(txn: Txn, dbi: Dbi) : AutoCloseable {
    private val arena = Arena()
    private val ptr: CPointer<MDB_cursor>

    init {
        memScoped {
            val ptrVar = arena.allocPointerTo<MDB_cursor>()
            check(mdb_cursor_open(txn.ptr, dbi.dbi, ptrVar.ptr))
            ptr = checkNotNull(ptrVar.value)
        }
    }

    internal actual fun get(option: CursorOption) : Result = memScoped {
        val key = alloc<MDB_val>()
        val data = alloc<MDB_val>()
        val result = checkRead(mdb_cursor_get(ptr, key.ptr, data.ptr, MDB_cursor_op.byValue(option.option)))
        return Result(result, key.toByteArray(), data.toByteArray())
    }

    internal actual fun get(key: ByteArray, option: CursorOption) : Result = memScoped {
        withMDB_val(key) { mdbKey ->
            val data = alloc<MDB_val>()
            val result = checkRead(mdb_cursor_get(ptr, mdbKey.ptr, data.ptr, MDB_cursor_op.byValue(option.option)))
            Result(result, key, data.toByteArray())
        }
    }

    internal actual fun get(key: ByteArray, data: ByteArray, option: CursorOption) : Result = memScoped {
        withMDB_val(key, data) { mdbKey, mdbData ->
            val result = checkRead(mdb_cursor_get(ptr, mdbKey.ptr, mdbData.ptr, MDB_cursor_op.byValue(option.option)))
            Result(result, key, mdbData.toByteArray())
        }
    }

    actual fun delete() {
        check(mdb_cursor_del(ptr, CursorDeleteOption.NONE.option))
    }

    actual fun deleteDuplicateData() {
        check(mdb_cursor_del(ptr, CursorDeleteOption.NO_DUP_DATA.option))
    }

    internal actual fun put(key: ByteArray, data: ByteArray, option: CursorPutOption): Result = memScoped {
        withMDB_val(key, data) { mdbKey, mdbData ->
            val result = check(mdb_cursor_put(ptr, mdbKey.ptr, mdbData.ptr, option.option))
            Result(result, key, data)
        }
    }

    actual override fun close() {
        mdb_cursor_close(ptr)
    }
}