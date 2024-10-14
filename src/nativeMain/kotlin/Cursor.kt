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

    internal actual fun get(option: CursorOption) : Triple<Int,Val,Val> {
        val key = Val.output()
        val data = Val.output()
        val result = mdb_cursor_get(ptr, key.mdbVal.ptr, data.mdbVal.ptr, MDB_cursor_op.byValue(option.option))
        return buildReadResult(result, key, data)
    }

    internal actual fun get(key: Val, option: CursorOption): Triple<Int, Val, Val> {
        val mdbData = Val.output()
        val result = mdb_cursor_get(ptr, key.mdbVal.ptr, mdbData.mdbVal.ptr, MDB_cursor_op.byValue(option.option))
        return buildReadResult(result, key, mdbData)
    }

    internal actual fun get(key: Val, data: Val, option: CursorOption): Triple<Int, Val, Val> {
        val result = mdb_cursor_get(ptr, key.mdbVal.ptr, data.mdbVal.ptr, MDB_cursor_op.byValue(option.option))
        return buildReadResult(result, key, data)
    }

    internal actual fun put(key: Val, data: Val, option: CursorPutOption): Triple<Int, Val, Val> {
        val result = mdb_cursor_put(ptr, key.mdbVal.ptr, data.mdbVal.ptr, option.option)
        return buildReadResult(result, key, data)
    }

    actual fun delete() {
        check(mdb_cursor_del(ptr, CursorDeleteOption.NONE.option))
    }

    actual fun deleteDuplicateData() {
        check(mdb_cursor_del(ptr, CursorDeleteOption.NO_DUP_DATA.option))
    }

    actual override fun close() {
        mdb_cursor_close(ptr)
    }
}