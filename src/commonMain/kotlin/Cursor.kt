expect class Cursor : AutoCloseable {
    internal fun get(option: CursorOption) : Triple<Int, Val, Val>
    internal fun get(key: Val, option: CursorOption) : Triple<Int, Val, Val>
    internal fun get(key: Val, data: Val, option: CursorOption) : Triple<Int, Val, Val>
    internal fun put(key: Val, data: Val, option: CursorPutOption) : Triple<Int, Val, Val>
    fun delete()
    fun deleteDuplicateData()
    fun countDuplicates(): ULong
    fun renew(txn: Txn)
    override fun close()
}

fun Cursor.next() = get(CursorOption.NEXT)

fun Cursor.nextDuplicate() = get(CursorOption.NEXT_DUP)

fun Cursor.nextNoDuplicate() = get(CursorOption.NEXT_NODUP)

fun Cursor.previous() = get(CursorOption.PREV)

fun Cursor.previousNoDuplicate() = get(CursorOption.PREV_NODUP)

fun Cursor.previousDuplicate() = get(CursorOption.PREV_DUP)

fun Cursor.last() = get(CursorOption.LAST)

fun Cursor.lastDuplicate() = get(CursorOption.LAST_DUP)

fun Cursor.first() = get(CursorOption.FIRST)

fun Cursor.firstDuplicate() = get(CursorOption.FIRST_DUP)

fun Cursor.getCurrent() = get(CursorOption.GET_CURRENT)

fun Cursor.getBoth(key: ByteArray, data: ByteArray) = get(key.toVal(), data.toVal(), CursorOption.GET_BOTH)

fun Cursor.getBoth(key: Val, data: Val) = get(key, data, CursorOption.GET_BOTH)

fun Cursor.getBothRange(key: ByteArray, data: ByteArray) = get(key.toVal(), data.toVal(), CursorOption.GET_BOTH_RANGE)

fun Cursor.getBothRange(key: Val, data: Val) = get(key, data, CursorOption.GET_BOTH_RANGE)

fun Cursor.set(key: ByteArray) = get(key.toVal(), CursorOption.SET)

fun Cursor.set(key: Val) = get(key, CursorOption.SET)

fun Cursor.setKey(key: ByteArray) = get(key.toVal(), CursorOption.SET_KEY)

fun Cursor.setKey(key: Val) = get(key, CursorOption.SET_KEY)

fun Cursor.setRange(key: ByteArray) = get(key.toVal(), CursorOption.SET_RANGE)

fun Cursor.setRange(key: Val) = get(key, CursorOption.SET_RANGE)

fun Cursor.put(key: ByteArray, data: ByteArray) = put(key.toVal(), data.toVal(), CursorPutOption.NONE)

fun Cursor.put(key: Val, data: Val) = put(key, data, CursorPutOption.NONE)

fun Cursor.putAppend(key: ByteArray, data: ByteArray) = put(key.toVal(), data.toVal(), CursorPutOption.APPEND)

fun Cursor.putAppend(key: Val, data: Val) = put(key, data, CursorPutOption.APPEND)

fun Cursor.putCurrent(key: ByteArray, data: ByteArray) = put(key.toVal(), data.toVal(), CursorPutOption.CURRENT)

fun Cursor.putCurrent(key: Val, data: Val) = put(key, data, CursorPutOption.CURRENT)

fun Cursor.putReserve(key: ByteArray, data: ByteArray) = put(key.toVal(), data.toVal(), CursorPutOption.RESERVE)

fun Cursor.putReserve(key: Val, data: Val) = put(key, data, CursorPutOption.RESERVE)

fun Cursor.putAppendDuplicate(key: ByteArray, data: ByteArray) = put(key.toVal(), data.toVal(), CursorPutOption.APPENDDUP)

fun Cursor.putAppendDuplicate(key: Val, data: Val) = put(key, data, CursorPutOption.APPENDDUP)

fun Cursor.putMultiple(key: ByteArray, data: ByteArray, size: Int) = put(key.toVal(), data.toVal(), CursorPutOption.MULTIPLE)

fun Cursor.putMultiple(key: Val, data: Val) = put(key, data, CursorPutOption.MULTIPLE)

fun Cursor.putNoDuplicateData(key: ByteArray, data: ByteArray) = put(key.toVal(), data.toVal(), CursorPutOption.NODUPDATA)

fun Cursor.putNoDuplicateData(key: Val, data: Val) = put(key, data, CursorPutOption.NODUPDATA)

fun Cursor.putNoOverwrite(key: ByteArray, data: ByteArray) = put(key.toVal(), data.toVal(), CursorPutOption.NOOVERWRITE)

fun Cursor.putNoOverwrite(key: Val, data: Val) = put(key, data, CursorPutOption.NOOVERWRITE)

fun Cursor.asSequence() : Sequence<Triple<Int, Val, Val>> = sequence {
    var hasNext = true
    while (hasNext) {
        val result = next()
        if (result.first == 0) {
            yield(result)
        } else {
            hasNext = false
            check(result.first)
        }
    }
}