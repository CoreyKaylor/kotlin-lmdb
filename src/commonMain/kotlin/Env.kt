expect class Env() : AutoCloseable {

    var maxDatabases: UInt
    var mapSize: ULong
    var maxReaders: UInt
    val stat: Stat?
    val info: EnvInfo?

    /**
     * Opens this environment at the provided [path] given the environment open [options]
     * @see EnvOption for convenience operators
     * [mode] (0664 default) The UNIX permissions to set on created files and semaphores.
     * This parameter is ignored on Windows.
     */
    fun open(path: String, vararg options: EnvOption, mode: UShort = 664u)

    fun beginTxn(vararg options: TxnOption) : Txn

    fun copyTo(path: String, compact: Boolean = false)

    fun sync(force: Boolean)

    override fun close()
}

inline fun Env.beginTxn(vararg options: TxnOption, crossinline block: (Txn) -> Unit) {
    beginTxn(*options).use {
        block(it)
    }
}
