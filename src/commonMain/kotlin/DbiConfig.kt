/**
 * Configuration for database open operations that can hold multiple comparers
 */
class DbiConfig {
    /**
     * Custom comparer for keys in the database
     */
    var keyComparer: ValComparer? = null
    
    /**
     * Custom comparer for duplicate values in the database (only used with MDB_DUPSORT)
     */
    var dupComparer: ValComparer? = null
    
    constructor()
    
    constructor(keyComparer: ValComparer? = null, dupComparer: ValComparer? = null) {
        this.keyComparer = keyComparer
        this.dupComparer = dupComparer
    }
}