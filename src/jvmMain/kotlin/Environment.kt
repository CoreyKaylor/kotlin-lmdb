import jnr.ffi.byref.PointerByReference
import Library.Companion.LMDB

actual class Environment : AutoCloseable {
    internal val env = PointerByReference()

    init {
        LMDB.mdb_env_create(env)
    }

    actual var maxDatabases: UInt = 0u
        set(value) {
            LMDB.mdb_env_set_maxdbs(env.value, value.toInt())
            field = value
        }

    actual var mapSize: ULong = 1024UL * 1024UL * 100UL
        set(value) {
            LMDB.mdb_env_set_mapsize(env.value, value.toLong())
            field = value
        }

    actual var maxReaders: UInt = 0u
        set(value) {
            LMDB.mdb_env_set_maxreaders(env.value, value.toInt())
            field = value
        }

    actual fun open(path: String, options: UInt, accessMode: UShort) {
        LMDB.mdb_env_open(env.value, path, options.toInt(), accessMode.toInt())
    }

    actual fun beginTransaction(options: UInt) : Transaction {
        val tx = Transaction(this)
        tx.beginTransaction(options)
        return tx
    }

    actual override fun close() {
        LMDB.mdb_env_close(env.value)
    }
}