expect class Txn : AutoCloseable {

    internal var state: TxnState
    internal constructor(env: Env, vararg options: TxnOption)
    internal constructor(env: Env, parent: Txn?, vararg options: TxnOption)
    
    /**
     * The transaction ID.
     * 
     * For a read-only transaction, this corresponds to the snapshot being read;
     * concurrent readers will frequently have the same transaction ID.
     * 
     * @return A transaction ID, valid if this transaction is active.
     */
    val id: ULong

    fun begin(vararg options: TxnOption) : Txn

    fun abort()

    fun reset()

    fun renew()

    fun commit()

    fun dbiOpen(name: String? = null, vararg options: DbiOption) : Dbi

    /**
     * Opens a database in the environment with custom comparers
     * 
     * @param name The name of the database to open, or null for the default database
     * @param config Configuration object that can contain key and duplicate value comparers
     * @param options Special options for this database
     * @return A database handle (Dbi)
     */
    fun dbiOpen(name: String?, config: DbiConfig, vararg options: DbiOption) : Dbi
    
    fun drop(dbi: Dbi)

    fun empty(dbi: Dbi)

    fun get(dbi: Dbi, key: Val) : Triple<Int,Val,Val>

    fun put(dbi: Dbi, key: Val, data: Val, vararg options: PutOption)

    fun delete(dbi: Dbi, key: Val)

    fun delete(dbi: Dbi, key: Val, data: Val)

    fun openCursor(dbi: Dbi) : Cursor

    override fun close()
}

fun Txn.get(dbi: Dbi, key: ByteArray) = this.get(dbi, key.toVal())

fun Txn.put(dbi: Dbi, key: ByteArray, data: ByteArray, vararg options: PutOption) =
    this.put(dbi, key.toVal(), data.toVal(), *options)

fun Txn.delete(dbi: Dbi, key: ByteArray) = this.delete(dbi, key.toVal())

fun Txn.delete(dbi: Dbi, key: ByteArray, data: ByteArray) = this.delete(dbi, key.toVal(), data.toVal())

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

typealias ValCompare = (Val, Val) -> Int
