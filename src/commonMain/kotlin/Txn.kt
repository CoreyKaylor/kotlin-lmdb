expect class Txn : AutoCloseable {

    internal var state: TxnState
    internal constructor(env: Env, vararg options: TxnOption)
    internal constructor(env: Env, parent: Txn?, vararg options: TxnOption)

    fun begin(vararg options: TxnOption) : Txn

    fun abort()

    fun reset()

    fun renew()

    fun commit()

    fun dbiOpen(name: String? = null, vararg options: DbiOption) : Dbi

    fun drop(dbi: Dbi)

    fun empty(dbi: Dbi)

    fun get(dbi: Dbi, key: ByteArray) : Result

    fun put(dbi: Dbi, key: ByteArray, data: ByteArray, vararg options: PutOption)

    fun delete(dbi: Dbi, key: ByteArray)

    fun delete(dbi: Dbi, key: ByteArray, data: ByteArray)

    fun openCursor(dbi: Dbi) : Cursor

    override fun close()
}

fun Txn.checkReady() {
    if (this.state != TxnState.Ready) {
        throw LmdbException("Transaction is in an invalid state to perform operation")
    }
}

enum class TxnState {
    Ready,
    Done,
    Reset,
    Released
}
