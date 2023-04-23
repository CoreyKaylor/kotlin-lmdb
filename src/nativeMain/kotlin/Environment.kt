import kotlinx.cinterop.*
import lmdb.*

actual class Environment : AutoCloseable {
    internal val mdbEnv: CPointerVar<MDB_env> = memScoped { alloc() }
    private var isOpened = false
    init {
        memScoped {
            check(mdb_env_create(mdbEnv.ptr))
        }
    }

    actual var maxDatabases: UInt = 0u
        set(value) {
            check(mdb_env_set_maxdbs(mdbEnv.value, value))
            field = value
        }

    actual var mapSize: ULong = 1024UL * 1024UL * 100UL
        set(value) {
            check(mdb_env_set_mapsize(mdbEnv.value, value))
            field = value
        }

    actual var maxReaders: UInt = 0u
        set(value) {
            check(mdb_env_set_maxreaders(mdbEnv.value, value))
            field = value
        }

    actual fun open(path: String, options: UInt, accessMode: UShort) {
        check(mdb_env_open(mdbEnv.value, path, options, accessMode))
        isOpened = true
    }

    actual fun beginTransaction(options: UInt) : Transaction {
        val tx = Transaction(this)
        tx.begin(options)
        return tx
    }

    actual override fun close() {
        if (!isOpened)
            return
        mdb_env_close(mdbEnv.value)
    }
}