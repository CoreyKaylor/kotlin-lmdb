expect class Dbi(name: String? = null, tx: Txn, vararg options: DbiOption) : AutoCloseable {
    fun stat(tx: Txn) : Stat
    fun compare(tx: Txn, a: Val, b: Val): Int
    fun dupCompare(tx: Txn, a: Val, b: Val): Int
    fun flags(tx: Txn): Set<DbiOption>
    
    /**
     * Close the database handle. Generally unnecessary unless you need to free up 
     * database slots for reuse. Safe to call multiple times; only the first call
     * has an effect.
     */
    override fun close()
}