expect class Environment() : AutoCloseable {

    var maxDatabases: UInt
    var mapSize: ULong
    var maxReaders: UInt

    /**
     * Opens this environment at the provided [path] given the environment open [options]
     * @see EnvironmentOpenOption for convenience operators
     * @see UnixAccessMode for convenience operators
     */
    fun open(path: String, options: UInt = EnvironmentOpenOption.None.option, accessMode: UShort = defaultUnixAccessMode)

    fun beginTransaction(options: UInt) : Transaction

    override fun close()
}