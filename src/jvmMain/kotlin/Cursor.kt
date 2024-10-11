import Library.Companion.LMDB
import jnr.ffi.Pointer
import jnr.ffi.byref.PointerByReference

actual class Cursor(txn: Txn, dbi: Dbi) : AutoCloseable {
    private val csrPtr = PointerByReference()
    private val ptr: Pointer
    private var closed = false

    init {
        check(LMDB.mdb_cursor_open(txn.ptr, dbi.ptr, csrPtr))
        ptr = csrPtr.value
    }

    internal actual fun get(option: CursorOption): Result {
        val key = MDBVal.output()
        val data = MDBVal.output()
        val result = checkRead(LMDB.mdb_cursor_get(ptr, key.ptr, data.ptr, option.option.toInt()))
        return Result(result, key, data)
    }

    internal actual fun get(key: ByteArray, option: CursorOption): Result {
        val mdbKey = MDBVal.input(key)
        val data = MDBVal.output()
        val result = checkRead(LMDB.mdb_cursor_get(ptr, mdbKey.ptr, data.ptr, option.option.toInt()))
        return Result(result, mdbKey, data)
    }

    internal actual fun get(key: ByteArray, data: ByteArray, option: CursorOption): Result {
        val mdbKey = MDBVal.input(key)
        val mdbData = MDBVal.input(data)
        val result = checkRead(LMDB.mdb_cursor_get(ptr, mdbKey.ptr, mdbData.ptr, option.option.toInt()))
        return Result(result, mdbKey, mdbData)
    }

    actual fun delete() {
        check(LMDB.mdb_cursor_del(ptr, CursorDeleteOption.NONE.option.toInt()))
    }

    actual fun deleteDuplicateData() {
        check(LMDB.mdb_cursor_del(ptr, CursorDeleteOption.NO_DUP_DATA.option.toInt()))
    }

    internal actual fun put(key: ByteArray, data: ByteArray, option: CursorPutOption): Result {
        val mdbKey = MDBVal.input(key)
        val mdbData = MDBVal.input(data)
        val result = check(LMDB.mdb_cursor_put(ptr, mdbKey.ptr, mdbData.ptr, option.option.toInt()))
        return Result(result, mdbKey, mdbData)
    }

    actual override fun close() {
        if(closed)
            return

        LMDB.mdb_cursor_close(ptr)
        closed = true
    }
}