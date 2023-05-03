import TxnState.*
import kotlinx.cinterop.*
import lmdb.*

actual class Txn internal actual constructor(env: Env, parent: Txn?, vararg options: TxnOption) : AutoCloseable {
    private val env: Env
    private val parentTx: CPointer<MDB_txn>?
    private var mdbTx: CPointerVar<MDB_txn> = memScoped { allocPointerTo() }
    internal val ptr: CPointer<MDB_txn>
    internal actual var state: TxnState
    internal actual constructor(env: Env, vararg options: TxnOption) : this(env, null, *options)

    init {
        this.env = env
        parentTx = parent?.ptr
        check(mdb_txn_begin(env.ptr, parentTx, options.asIterable().toFlags(), mdbTx.ptr))
        ptr = mdbTx.value!!.pointed.ptr
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

    actual fun get(dbi: Dbi, key: ByteArray) : Result {
        val mdbKey = key.toMDB_val()
        val mdbVal = cValue<MDB_val>()
        return memScoped {
            val code = checkRead(mdb_get(ptr, dbi.dbi, mdbKey, mdbVal))
            Result(code, mdbKey.ptr.pointed, mdbVal.ptr.pointed)
        }
    }

    actual fun put(dbi: Dbi, key: ByteArray, data: ByteArray, vararg options: PutOption) {
        val mdbKey = key.toMDB_val()
        val mdbData = data.toMDB_val()
        check(mdb_put(ptr, dbi.dbi, mdbKey, mdbData, options.asIterable().toFlags()))
    }

    actual override fun close() {
        if(state == Released) {
            return
        }
        if (state == Ready) {
            mdb_txn_abort(ptr)
        }
        state = Released
    }
}