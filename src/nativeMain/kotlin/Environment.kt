import kotlinx.cinterop.*
import lmdb.*

actual class Environment : AutoCloseable {
    internal val env: CPointerVar<MDB_env> = memScoped { alloc() }
    private var isOpened = false
    init {
        check(mdb_env_create(env.ptr))
    }

    actual var maxDatabases: UInt = 0u
        set(value) {
            check(mdb_env_set_maxdbs(env.value, value))
            field = value
        }

    actual var mapSize: ULong = 1024UL * 1024UL * 100UL
        set(value) {
            check(mdb_env_set_mapsize(env.value, value))
            field = value
        }

    actual var maxReaders: UInt = 0u
        set(value) {
            check(mdb_env_set_maxreaders(env.value, value))
            field = value
        }

    actual val stat: EnvironmentStat?
        get() {
        val statPtr: CPointerVar<MDB_stat> = memScoped { alloc() }
        check(mdb_env_stat(env.value, statPtr.value))
        val pointed = statPtr.pointed!!
        return EnvironmentStat(
            pointed.ms_branch_pages, pointed.ms_depth, pointed.ms_entries, pointed.ms_leaf_pages,
            pointed.ms_overflow_pages, pointed.ms_psize
        )
    }

    actual val info: EnvironmentInfo?
        get() {
            val envInfo: CPointerVar<MDB_envinfo> = memScoped { alloc() }
            mdb_env_info(env.value, envInfo.value)
            val pointed = envInfo.pointed!!
            return EnvironmentInfo(pointed.me_last_pgno, pointed.me_last_txnid,
                pointed.me_mapaddr.toLong().toULong(), pointed.me_mapsize, pointed.me_maxreaders, pointed.me_numreaders)
        }

    actual fun open(path: String, options: UInt, accessMode: UShort) {
        check(mdb_env_open(env.value, path, options, accessMode))
        isOpened = true
    }

    actual fun beginTransaction(options: UInt) : Transaction {
        val tx = Transaction(this)
        tx.begin(options)
        return tx
    }

    actual fun copyTo(path: String, compact: Boolean) {
        val flags = if (compact) {
            0x01u
        } else {
            0u
        }
        check(mdb_env_copy2(env.value, path, flags))
    }

    actual fun sync(force: Boolean) {
        val forceInt = if(force) 1 else 0
        check(mdb_env_sync(env.value, forceInt))
    }

    actual override fun close() {
        if (!isOpened)
            return
        mdb_env_close(env.value)
    }
}