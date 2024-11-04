import kotlinx.cinterop.*
import lmdb.*

actual class Env : AutoCloseable {
    internal val ptr: CPointer<MDB_env>

    init {
        ptr = memScoped {
            val ptrVar = allocPointerTo<MDB_env>()
            check(mdb_env_create(ptrVar.ptr))
            checkNotNull(ptrVar.value)
        }
    }


    var isOpened: Boolean = false
        private set
    private var isClosed = false

    actual var maxDatabases: UInt = 0u
        set(value) {
            check(mdb_env_set_maxdbs(ptr, value))
            field = value
        }

    actual var mapSize: ULong = 1024UL * 1024UL * 50UL
        set(value) {
            check(mdb_env_set_mapsize(ptr, value))
            field = value
        }

    actual var maxReaders: UInt = 0u
        set(value) {
            check(mdb_env_set_maxreaders(ptr, value))
            field = value
        }

    actual val stat: Stat?
        get() {
            return memScoped {
                val statPtr = alloc<MDB_stat>()
                check(mdb_env_stat(ptr, statPtr.ptr))
                Stat(
                    statPtr.ms_branch_pages, statPtr.ms_depth, statPtr.ms_entries, statPtr.ms_leaf_pages,
                    statPtr.ms_overflow_pages, statPtr.ms_psize
                )
            }
        }

    actual val info: EnvInfo?
        get() {
            return memScoped {
            val envInfo = alloc<MDB_envinfo>()
            check(mdb_env_info(ptr, envInfo.ptr))
                EnvInfo(envInfo.me_last_pgno, envInfo.me_last_txnid, envInfo.me_mapaddr.toLong().toULong(),
                    envInfo.me_mapsize, envInfo.me_maxreaders, envInfo.me_numreaders)
            }
        }

    actual fun open(path: String, vararg options: EnvOption, mode: String) {
        isOpened = true
        val result = mdb_env_open(ptr, path, options.asIterable().toFlags(), mode.toUShort(8))
        check(result)
    }

    actual fun beginTxn(vararg options: TxnOption) : Txn {
        return Txn(this, *options)
    }

    actual fun copyTo(path: String, compact: Boolean) {
        val flags = if (compact) {
            0x01u
        } else {
            0u
        }
        check(mdb_env_copy2(ptr, path, flags))
    }

    actual fun sync(force: Boolean) {
        val forceInt = if(force) 1 else 0
        check(mdb_env_sync(ptr, forceInt))
    }

    actual override fun close() {
        if (isClosed)
            return
        if (isOpened) {
            mdb_env_close(ptr)
        }
        isClosed = true
    }
}