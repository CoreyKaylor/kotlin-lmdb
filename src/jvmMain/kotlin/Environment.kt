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

    actual val stat: EnvironmentStat?
        get() {
            val mdbStat: Library.MDB_stat = Library.MDB_stat(Library.RUNTIME)
            LMDB.mdb_env_stat(env.value, mdbStat)
            return EnvironmentStat(mdbStat.f2_ms_branch_pages.get().toULong(), mdbStat.f1_ms_depth.get().toUInt(),
                mdbStat.f5_ms_entries.get().toULong(), mdbStat.f3_ms_leaf_pages.get().toULong(),
                mdbStat.f4_ms_overflow_pages.get().toULong(), mdbStat.f0_ms_psize.get().toUInt())
        }

    actual val info: EnvironmentInfo?
        get() {
            val mdbEnvInfo: Library.MDB_envinfo = Library.MDB_envinfo(Library.RUNTIME)
            LMDB.mdb_env_info(env.value, mdbEnvInfo)
            return EnvironmentInfo(mdbEnvInfo.f2_me_last_pgno.get().toULong(), mdbEnvInfo.f3_me_last_txnid.get().toULong(),
                mdbEnvInfo.f0_me_mapaddr.get().address().toULong(), mdbEnvInfo.f1_me_mapsize.get().toULong(),
                mdbEnvInfo.f4_me_maxreaders.get().toUInt(), mdbEnvInfo.f5_me_numreaders.get().toUInt())
        }

    actual fun open(path: String, options: UInt, accessMode: UShort) {
        LMDB.mdb_env_open(env.value, path, options.toInt(), accessMode.toInt())
    }

    actual fun beginTransaction(options: UInt) : Transaction {
        val tx = Transaction(this)
        tx.beginTransaction(options)
        return tx
    }

    actual fun copyTo(path: String, compact: Boolean) {
        val flags = if (compact) {
            0x01
        } else {
            0
        }
        LMDB.mdb_env_copy2(env.value, path, flags)
    }

    actual fun sync(force: Boolean) {
        val forceInt = if (force) 1 else 0
        LMDB.mdb_env_sync(env.value, forceInt)
    }

    actual override fun close() {
        LMDB.mdb_env_close(env.value)
    }
}