import kotlinx.cinterop.*
import lmdb.*

actual class Environment : AutoCloseable {
    private val mdbEnv: CPointerVar<MDB_env> = memScoped { alloc() }
    private var isOpened = false
    init {
        memScoped {
            check(mdb_env_create(mdbEnv.ptr))
        }
    }

    actual fun open(path: String, options: Set<EnvironmentOpenOption>) {
        val flags = options.toFlags()
        memScoped {
            check(mdb_env_open(mdbEnv.value, path, flags, 438))
            isOpened = true
        }
    }

    actual override fun close() {
        if (!isOpened)
            return
        memScoped {
            mdb_env_close(mdbEnv.value)
        }
    }
}