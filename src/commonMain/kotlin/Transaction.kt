expect class Transaction : AutoCloseable {
    internal constructor(environment: Environment)
    internal constructor(environment: Environment, parent: Transaction)

    internal fun beginTransaction(options: UInt = TransactionBeginOptions.None.option)

    fun begin(options: UInt = TransactionBeginOptions.None.option) : Transaction

    override fun close()
}