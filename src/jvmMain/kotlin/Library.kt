import jnr.ffi.LibraryLoader
import jnr.ffi.Platform
import jnr.ffi.Pointer
import jnr.ffi.Runtime
import jnr.ffi.Struct
import jnr.ffi.annotations.Delegate
import jnr.ffi.annotations.In
import jnr.ffi.annotations.Out
import jnr.ffi.byref.IntByReference
import jnr.ffi.byref.NativeLongByReference
import jnr.ffi.byref.PointerByReference
import jnr.ffi.provider.MemoryManager
import jnr.ffi.types.size_t
import java.util.*

internal class Library {
    companion object {
        const val LMDB_NATIVE_LIB_PROP = "lmdb.native.lib"

        val LMDB: Lmdb
        val RUNTIME: Runtime
        val MEMORY: MemoryManager

        val SHOULD_USE_LIB = Objects.nonNull(
            System.getProperty(LMDB_NATIVE_LIB_PROP)
        )
        private const val LIB_NAME = "lmdb"

        init {
            val libToLoad = if (SHOULD_USE_LIB) {
                System.getProperty(LMDB_NATIVE_LIB_PROP)
            } else {
                Platform.getNativePlatform().mapLibraryName(LIB_NAME)
            }
            LMDB = LibraryLoader.create(Lmdb::class.java)
                .searchDefault()
                .load(libToLoad)
            RUNTIME = Runtime.getRuntime(LMDB)
            MEMORY = RUNTIME.memoryManager
        }
    }



    class MDB_envinfo internal constructor(runtime: Runtime?) : Struct(runtime) {
        val f0_me_mapaddr: Pointer
        val f1_me_mapsize: size_t
        val f2_me_last_pgno: size_t
        val f3_me_last_txnid: size_t
        val f4_me_maxreaders: u_int32_t
        val f5_me_numreaders: u_int32_t

        init {
            f0_me_mapaddr = Pointer()
            f1_me_mapsize = size_t()
            f2_me_last_pgno = size_t()
            f3_me_last_txnid = size_t()
            f4_me_maxreaders = u_int32_t()
            f5_me_numreaders = u_int32_t()
        }
    }

    class MDB_stat internal constructor(runtime: Runtime?) : Struct(runtime) {
        val f0_ms_psize: u_int32_t
        val f1_ms_depth: u_int32_t
        val f2_ms_branch_pages: size_t
        val f3_ms_leaf_pages: size_t
        val f4_ms_overflow_pages: size_t
        val f5_ms_entries: size_t

        init {
            f0_ms_psize = u_int32_t()
            f1_ms_depth = u_int32_t()
            f2_ms_branch_pages = size_t()
            f3_ms_leaf_pages = size_t()
            f4_ms_overflow_pages = size_t()
            f5_ms_entries = size_t()
        }
    }

    interface ComparatorCallback {
        @Delegate
        fun compare(@In keyA: Pointer?, @In keyB: Pointer?): Int
    }

    interface Lmdb {
        fun mdb_cursor_close(@In cursor: Pointer?)
        fun mdb_cursor_count(@In cursor: Pointer?, countp: NativeLongByReference?): Int
        fun mdb_cursor_del(@In cursor: Pointer?, flags: Int): Int
        fun mdb_cursor_get(@In cursor: Pointer?, k: Pointer?, @Out v: Pointer?, cursorOp: Int): Int
        fun mdb_cursor_open(@In txn: Pointer?, @In dbi: Pointer?, cursorPtr: PointerByReference?): Int
        fun mdb_cursor_put(@In cursor: Pointer?, @In key: Pointer?, @In data: Pointer?, flags: Int): Int
        fun mdb_cursor_renew(@In txn: Pointer?, @In cursor: Pointer?): Int
        fun mdb_dbi_close(@In env: Pointer?, @In dbi: Pointer?)
        fun mdb_dbi_flags(@In txn: Pointer?, @In dbi: Pointer?, @Out flags: IntByReference?): Int
        fun mdb_dbi_open(@In txn: Pointer?, @In name: ByteArray?, flags: Int, @In dbiPtr: Pointer?): Int
        fun mdb_del(@In txn: Pointer?, @In dbi: Pointer?, @In key: Pointer?, @In data: Pointer?): Int
        fun mdb_drop(@In txn: Pointer?, @In dbi: Pointer?, del: Int): Int
        fun mdb_env_close(@In env: Pointer?)
        fun mdb_env_copy2(@In env: Pointer?, @In path: String?, flags: Int): Int
        fun mdb_env_create(envPtr: PointerByReference?): Int
        fun mdb_env_get_fd(@In env: Pointer?, @In fd: Pointer?): Int
        fun mdb_env_get_flags(@In env: Pointer?, flags: Int): Int
        fun mdb_env_get_maxkeysize(@In env: Pointer?): Int
        fun mdb_env_get_maxreaders(@In env: Pointer?, readers: Int): Int
        fun mdb_env_get_path(@In env: Pointer?, path: String?): Int
        fun mdb_env_info(@In env: Pointer?, @Out info: MDB_envinfo?): Int
        fun mdb_env_open(@In env: Pointer?, @In path: String?, flags: Int, mode: Int): Int
        fun mdb_env_set_flags(@In env: Pointer?, flags: Int, onoff: Int): Int
        fun mdb_env_set_mapsize(@In env: Pointer?, @size_t size: Long): Int
        fun mdb_env_set_maxdbs(@In env: Pointer?, dbs: Int): Int
        fun mdb_env_set_maxreaders(@In env: Pointer?, readers: Int): Int
        fun mdb_env_stat(@In env: Pointer?, @Out stat: MDB_stat?): Int
        fun mdb_env_sync(@In env: Pointer?, f: Int): Int
        fun mdb_get(@In txn: Pointer?, @In dbi: Pointer?, @In key: Pointer?, @Out data: Pointer?): Int
        fun mdb_put(@In txn: Pointer?, @In dbi: Pointer?, @In key: Pointer?, @In data: Pointer?, flags: Int): Int
        fun mdb_reader_check(@In env: Pointer?, @Out dead: IntByReference?): Int
        fun mdb_set_compare(@In txn: Pointer?, @In dbi: Pointer?, cb: ComparatorCallback?): Int
        fun mdb_stat(@In txn: Pointer?, @In dbi: Pointer?, @Out stat: MDB_stat?): Int
        fun mdb_strerror(rc: Int): String?
        fun mdb_txn_abort(@In txn: Pointer?)
        fun mdb_txn_begin(@In env: Pointer?, @In parentTx: Pointer?, flags: Int, txPtr: Pointer?): Int
        fun mdb_txn_commit(@In txn: Pointer?): Int
        fun mdb_txn_env(@In txn: Pointer?): Pointer?
        fun mdb_txn_id(@In txn: Pointer?): Long
        fun mdb_txn_renew(@In txn: Pointer?): Int
        fun mdb_txn_reset(@In txn: Pointer?)
        fun mdb_version(major: IntByReference?, minor: IntByReference?, patch: IntByReference?): Pointer?
    }
}