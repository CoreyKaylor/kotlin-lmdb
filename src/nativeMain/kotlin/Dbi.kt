import kotlinx.cinterop.*
import lmdb.*

actual class Dbi actual constructor(name: String?, private val tx: Txn, vararg options: DbiOption) {
    private val arena = Arena()
    private val dbiPtr = arena.alloc<MDB_dbiVar>()
    internal val dbi: MDB_dbi

    init {
        check(mdb_dbi_open(tx.ptr, name, options.asIterable().toFlags(), dbiPtr.ptr))
        dbi = dbiPtr.ptr.pointed.value
    }

    actual fun stat(tx: Txn) : Stat {
        val statPtr: CValue<MDB_stat> = cValue<MDB_stat>()
        memScoped {
            check(mdb_stat(tx.ptr, dbi, statPtr))
        }
        val pointed = memScoped {
            statPtr.ptr.pointed
        }
        return Stat(
            pointed.ms_branch_pages, pointed.ms_depth, pointed.ms_entries, pointed.ms_leaf_pages,
            pointed.ms_overflow_pages, pointed.ms_psize
        )
    }
}