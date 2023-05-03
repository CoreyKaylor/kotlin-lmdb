import kotlinx.cinterop.*
import lmdb.*

actual class Dbi actual constructor(name: String?, tx: Txn, vararg options: DbiOption) {
    private var dbiPtr: CValue<MDB_dbiVar> = cValue<MDB_dbiVar>()
    internal val dbi: MDB_dbi

    init {
        check(mdb_dbi_open(tx.ptr, name, options.asIterable().toFlags(), dbiPtr))
        dbi = memScoped {
            dbiPtr.ptr[0]
        }
    }

    actual fun stat(tx: Txn) : Stat {
        val statPtr: CValue<MDB_stat> = cValue<MDB_stat>()
        check(mdb_stat(tx.ptr, dbi, statPtr))
        val pointed = memScoped {
            statPtr.ptr.pointed
        }
        return Stat(
            pointed.ms_branch_pages, pointed.ms_depth, pointed.ms_entries, pointed.ms_leaf_pages,
            pointed.ms_overflow_pages, pointed.ms_psize
        )
    }
}