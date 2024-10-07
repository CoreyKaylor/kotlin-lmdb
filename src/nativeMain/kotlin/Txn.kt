import TxnState.*
import kotlinx.cinterop.*
import lmdb.*

actual class Txn internal actual constructor(env: Env, parent: Txn?, vararg options: TxnOption) : AutoCloseable {
    private val arena = Arena()
    private val env: Env
    private val parentTx: CPointer<MDB_txn>?
    internal val ptr: CPointer<MDB_txn>
    internal actual var state: TxnState
    private var isClosed = false

    internal actual constructor(env: Env, vararg options: TxnOption) : this(env, null, *options)

    init {
        this.env = env
        parentTx = parent?.ptr
        val ptrVar = arena.allocPointerTo<MDB_txn>()
        check(mdb_txn_begin(env.ptr, parent?.ptr, options.asIterable().toFlags(), ptrVar.ptr))
        ptr = checkNotNull(ptrVar.value)
        state = Ready
    }

    actual fun begin(vararg options: TxnOption) : Txn {
        return Txn(env, this)
    }

    actual fun abort() {
        checkReady()
        state = Done
        mdb_txn_abort(ptr)
    }

    actual fun reset() {
        when(state) {
            Ready, Done -> throw LmdbException("Transaction is in an invalid state for reset.")
            Reset, Released -> state = Reset
        }
        mdb_txn_reset(ptr)
    }

    actual fun renew() {
        if (state != Reset) {
            throw LmdbException("Transaction is in an invalid state for renew, must be reset.")
        }
        state = Done
        check(mdb_txn_renew(ptr))
        state = Ready
    }

    actual fun commit() {
        checkReady()
        state = Done
        check(mdb_txn_commit(ptr))
    }

    actual fun dbiOpen(name: String?, vararg options: DbiOption) : Dbi {
        return Dbi(name, this, *options)
    }

    actual fun get(dbi: Dbi, key: ByteArray) : Result = memScoped {
        return toPinnedMDB_val(key) { mdbKey ->
            val mdbData = cValue<MDB_val>()
            val code = checkRead(mdb_get(ptr, dbi.dbi, mdbKey.ptr, mdbData.ptr))
            Result(code, mdbKey, mdbData.ptr.pointed)
        }
    }

    actual fun put(dbi: Dbi, key: ByteArray, data: ByteArray, vararg options: PutOption) {
        memScoped {
            toPinnedMDB_val(key, data) {mdbKey, mdbData ->
                check(mdb_put(ptr, dbi.dbi, mdbKey.ptr, mdbData.ptr, options.asIterable().toFlags()))
            }
        }
    }

    actual override fun close() {
        if(isClosed)
            return
        isClosed = true
        if(state == Released) {
            return
        }
        if (state == Ready) {
            mdb_txn_abort(ptr)
        }
        state = Released
    }
}