import Library.Companion.LMDB
import jnr.ffi.byref.PointerByReference

actual class Transaction : AutoCloseable {
    private val env: Environment
    private val tx = PointerByReference()
    private val parentTx: PointerByReference?
    internal actual constructor(environment: Environment) {
        env = environment
        parentTx = null
    }

    internal actual constructor(environment: Environment, parent: Transaction) {
        env = environment
        parentTx = parent.tx
    }

    actual fun beginTransaction(options: UInt) {
        LMDB.mdb_txn_begin(env.env.value, parentTx?.value, options.toInt(), tx.value)
    }

    actual fun begin(options: UInt) : Transaction {
        val nestedTx = Transaction(env, this)
        nestedTx.beginTransaction(options)
        return nestedTx
    }

    actual override fun close() {
    }
}