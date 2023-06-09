import Library.Companion.LMDB
import Library.*
import Library.Companion.RUNTIME
import TxnState.*
import jnr.ffi.Memory.allocateDirect
import jnr.ffi.NativeType
import jnr.ffi.Pointer

actual class Txn internal actual constructor(env: Env, parent: Txn?, vararg options: TxnOption) : AutoCloseable {
    private val env: Env
    private val txnPtr = allocateDirect(RUNTIME, NativeType.ADDRESS)
    internal val ptr: Pointer
    private val parentTx: Pointer?
    internal actual var state: TxnState

    internal actual constructor(env: Env, vararg options: TxnOption) : this(env, null, *options)

    init {
        this.env = env
        parentTx = parent?.ptr
        check(LMDB.mdb_txn_begin(env.ptr, parentTx, options.asIterable().toFlags().toInt(), txnPtr))
        ptr = txnPtr.getPointer(0)
        state = Ready
    }

    actual fun begin(vararg options: TxnOption) : Txn {
        return Txn(env, this)
    }

    actual fun abort() {
        checkReady()
        LMDB.mdb_txn_abort(ptr)
        state = Done
    }

    actual fun reset() {
        when(state) {
            Ready, Done -> throw LmdbException("Transaction is in an invalid state for reset.")
            Reset, Released -> state = Reset
        }
        LMDB.mdb_txn_reset(ptr)
    }

    actual fun renew() {
       if (state != Reset) {
           throw LmdbException("Transaction is in an invalid state for renew, must be reset.")
       }
        state = Done
        check(LMDB.mdb_txn_renew(ptr))
        state = Ready
    }

    actual fun commit() {
        checkReady()
        state = Done
        check(LMDB.mdb_txn_commit(ptr))
    }

    actual fun dbiOpen(name: String?, vararg options: DbiOption) : Dbi {
        return Dbi(name, this, *options)
    }

    actual fun get(dbi: Dbi, key: ByteArray) : Result {
        val keyPtr = MDBVal.input(key)
        val dataPtr = MDBVal.output()
        val resultCode = check(LMDB.mdb_get(ptr, dbi.ptr, keyPtr.ptr, dataPtr.ptr))
        return Result(resultCode, keyPtr, dataPtr)
    }

    actual fun put(dbi: Dbi, key: ByteArray, data: ByteArray, vararg options: PutOption) {
        val keyPtr = MDBVal.input(key)
        val dataPtr = MDBVal.input(data)
        check(LMDB.mdb_put(ptr, dbi.ptr, keyPtr.ptr, dataPtr.ptr,
            options.asIterable().toFlags().toInt()))
    }

    actual override fun close() {
        if(state == Released) {
            return
        }
        if (state == Ready) {
            LMDB.mdb_txn_abort(ptr)
        }
        state = Released
    }
}
