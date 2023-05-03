import jnr.ffi.byref.PointerByReference
import Library.Companion.LMDB
import jnr.ffi.Pointer

actual class Env : AutoCloseable {
    private val envPtr = PointerByReference()
    internal val ptr: Pointer

    init {
        LMDB.mdb_env_create(envPtr)
        ptr = envPtr.value
    }

    actual var maxDatabases: UInt = 0u
        set(value) {
            LMDB.mdb_env_set_maxdbs(ptr, value.toInt())
            field = value
        }

    actual var mapSize: ULong = 1024UL * 1024UL * 100UL
        set(value) {
            LMDB.mdb_env_set_mapsize(ptr, value.toLong())
            field = value
        }

    actual var maxReaders: UInt = 0u
        set(value) {
            LMDB.mdb_env_set_maxreaders(ptr, value.toInt())
            field = value
        }

    actual val stat: Stat?
        get() {
            val mdbStat: Library.MDB_stat = Library.MDB_stat(Library.RUNTIME)
            LMDB.mdb_env_stat(ptr, mdbStat)
            return Stat(mdbStat.f2_ms_branch_pages.get().toULong(), mdbStat.f1_ms_depth.get().toUInt(),
                mdbStat.f5_ms_entries.get().toULong(), mdbStat.f3_ms_leaf_pages.get().toULong(),
                mdbStat.f4_ms_overflow_pages.get().toULong(), mdbStat.f0_ms_psize.get().toUInt())
        }

    actual val info: EnvInfo?
        get() {
            val mdbEnvInfo: Library.MDB_envinfo = Library.MDB_envinfo(Library.RUNTIME)
            check(LMDB.mdb_env_info(ptr, mdbEnvInfo))
            return EnvInfo(mdbEnvInfo.f2_me_last_pgno.get().toULong(), mdbEnvInfo.f3_me_last_txnid.get().toULong(),
                mdbEnvInfo.f0_me_mapaddr.get().address().toULong(), mdbEnvInfo.f1_me_mapsize.get().toULong(),
                mdbEnvInfo.f4_me_maxreaders.get().toUInt(), mdbEnvInfo.f5_me_numreaders.get().toUInt())
        }

    actual fun open(path: String, vararg options: EnvOption, mode: UShort) {
        check(LMDB.mdb_env_open(ptr, path, options.asIterable().toFlags().toInt(), mode.toInt()))
    }

    actual fun beginTxn(vararg options: TxnOption) : Txn {
        return Txn(this, *options)
    }

    actual fun copyTo(path: String, compact: Boolean) {
        val flags = if (compact) {
            0x01
        } else {
            0
        }
        check(LMDB.mdb_env_copy2(ptr, path, flags))
    }

    actual fun sync(force: Boolean) {
        val forceInt = if (force) 1 else 0
        check(LMDB.mdb_env_sync(ptr, forceInt))
    }

    actual override fun close() {
        LMDB.mdb_env_close(ptr)
    }
}