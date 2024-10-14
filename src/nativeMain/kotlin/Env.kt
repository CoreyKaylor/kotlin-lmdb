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


    private var _isOpened = false
    var isOpened: Boolean
        get() = _isOpened
        private set(value) {
            _isOpened = value
        }
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
        val statPtr: CValue<MDB_stat> = cValue<MDB_stat>()
        check(mdb_env_stat(ptr, statPtr))
        val pointed = memScoped {
            statPtr.ptr.pointed
        }
        return Stat(
            pointed.ms_branch_pages, pointed.ms_depth, pointed.ms_entries, pointed.ms_leaf_pages,
            pointed.ms_overflow_pages, pointed.ms_psize
        )
    }

    actual val info: EnvInfo?
        get() {
            val envInfo: CValue<MDB_envinfo> = cValue<MDB_envinfo>()
            check(mdb_env_info(ptr, envInfo))
            val pointed = memScoped {
                envInfo.ptr.pointed
            }
            return EnvInfo(pointed.me_last_pgno, pointed.me_last_txnid,
                pointed.me_mapaddr.toLong().toULong(), pointed.me_mapsize, pointed.me_maxreaders, pointed.me_numreaders)
        }

    actual fun open(path: String, vararg options: EnvOption, mode: UShort) {
        isOpened = true
        check(mdb_env_open(ptr, path, options.asIterable().toFlags(), mode))
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