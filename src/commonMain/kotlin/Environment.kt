expect class Environment() : AutoCloseable {

    /**
     * Opens this environment at the provided [path] given the environment open [options]
     * @see EnvironmentOpenOption
     */
    fun open(path: String, options: Set<EnvironmentOpenOption> = emptySet())

    override fun close()
}