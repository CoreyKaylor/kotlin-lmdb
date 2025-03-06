expect class Dbi(name: String? = null, tx: Txn, vararg options: DbiOption) {
    fun stat(tx: Txn) : Stat
    fun compare(tx: Txn, a: Val, b: Val): Int
}