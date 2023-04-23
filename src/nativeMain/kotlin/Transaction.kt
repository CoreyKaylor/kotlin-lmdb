import kotlinx.cinterop.*
import lmdb.MDB_txn
import lmdb.mdb_txn_begin

actual class Transaction : AutoCloseable {
    private val env: Environment
    private val parentTx: CPointerVar<MDB_txn>?
    private var mdbTx: CPointerVar<MDB_txn> = memScoped { alloc() }
    internal actual constructor(environment: Environment) {
        env = environment
        parentTx = null
    }

    internal actual constructor(environment: Environment, parent: Transaction) {
        env = environment
        parentTx = parent.mdbTx
    }

    actual fun beginTransaction(options: UInt) {
        mdb_txn_begin(env.mdbEnv.value, parentTx?.value, options, mdbTx.ptr)
    }

    actual fun begin(options: UInt) : Transaction {
        val nestedTx = Transaction(env, this)
        nestedTx.beginTransaction(options)
        return nestedTx
    }

    actual override fun close() {
    }
}