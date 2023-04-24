expect class Transaction : AutoCloseable {
    internal constructor(environment: Environment)
    internal constructor(environment: Environment, parent: Transaction)

    internal fun beginTransaction(options: UInt = TransactionOptions.None.option)

    fun begin(options: UInt = TransactionOptions.None.option) : Transaction

    override fun close()
}