import jnr.ffi.byref.PointerByReference
import Library.Companion.LMDB

actual class Environment : AutoCloseable {
    private val env = PointerByReference()

    init {
        LMDB.mdb_env_create(env)
    }

    actual fun open(path: String, options: Set<EnvironmentOpenOption>) {
        val flags = options.toFlags()
        LMDB.mdb_env_open(env.value, path, flags.toInt(), 0)
    }

    actual override fun close() {
        LMDB.mdb_env_close(env.value)
    }
}