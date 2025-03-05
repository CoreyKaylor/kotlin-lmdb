expect class Env() : AutoCloseable {

    var maxDatabases: UInt
    var mapSize: ULong
    var maxReaders: UInt
    /**
     * Returns the maximum size of keys and MDB_DUPSORT data we can write.
     * This is a limit of the LMDB database that can't be changed and is determined
     * at compile time.
     */
    val maxKeySize: UInt
    /**
     * Returns the number of stale readers that were cleared.
     *
     * Stale readers are transactions that were not properly closed and are still
     * taking up space in the lock table. This property checks for such stale entries
     * and removes them, returning the count of entries that were removed.
     * 
     * @return The number of stale readers that were cleared from the lock table
     */
    val staleReaderCount: UInt
    val stat: Stat?
    val info: EnvInfo?
    var flags: Set<EnvOption>

    /**
     * Opens this environment at the provided [path] given the environment open [options]
     * @see EnvOption for convenience operators
     * [mode] (0664 default) The UNIX permissions to set on created files and semaphores.
     * This parameter is ignored on Windows.
     */
    fun open(path: String, vararg options: EnvOption, mode: String = "0664")

    fun beginTxn(vararg options: TxnOption) : Txn

    /**
     * Copies the environment to the specified path with compaction option
     * 
     * @param path The directory path where the copy will be stored
     * @param compact If true, perform compaction while copying
     */
    fun copyTo(path: String, compact: Boolean = false)
    
    /**
     * Copies the environment to the specified path
     * 
     * This is a simpler version of copyTo that doesn't support compaction
     * 
     * @param path The directory path where the copy will be stored
     */
    fun copyTo(path: String)

    fun sync(force: Boolean)

    override fun close()
}

inline fun Env.beginTxn(vararg options: TxnOption, crossinline block: Txn.() -> Unit) {
    beginTxn(*options).use {
        block(it)
    }
}
