expect class Cursor : AutoCloseable {
    internal fun get(option: CursorOption) : Result
    internal fun get(key:ByteArray, option: CursorOption) : Result
    internal fun get(key:ByteArray, data: ByteArray, option: CursorOption) : Result
    internal fun put(key:ByteArray, data: ByteArray, option: CursorPutOption) : Result
    fun delete()
    fun deleteDuplicateData()
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

fun Cursor.getBoth(key: ByteArray, data: ByteArray) = get(key, data, CursorOption.GET_BOTH)

fun Cursor.getBothRange(key: ByteArray, data: ByteArray) = get(key, data, CursorOption.GET_BOTH_RANGE)

fun Cursor.set(key: ByteArray) = get(key, CursorOption.SET)

fun Cursor.setKey(key: ByteArray) = get(key, CursorOption.SET_KEY)

fun Cursor.setRange(key: ByteArray) = get(key, CursorOption.SET_RANGE)

fun Cursor.put(key: ByteArray, data: ByteArray) = put(key, data, CursorPutOption.NONE)

fun Cursor.putAppend(key: ByteArray, data: ByteArray) = put(key, data, CursorPutOption.APPEND)

fun Cursor.putCurrent(key: ByteArray, data: ByteArray) = put(key, data, CursorPutOption.CURRENT)

fun Cursor.putReserve(key: ByteArray, data: ByteArray) = put(key, data, CursorPutOption.RESERVE)

fun Cursor.putAppendDuplicate(key: ByteArray, data: ByteArray) = put(key, data, CursorPutOption.APPENDDUP)

fun Cursor.putMultiple(key: ByteArray, data: ByteArray, size: Int) = put(key, data, CursorPutOption.MULTIPLE)

fun Cursor.putNoDuplicateData(key: ByteArray, data: ByteArray) = put(key, data, CursorPutOption.NODUPDATA)

fun Cursor.putNoOverwrite(key: ByteArray, data: ByteArray) = put(key, data, CursorPutOption.NOOVERWRITE)

fun Cursor.asSequence() = sequence {
    var hasNext = true
    while (hasNext) {
        val result = next()
        if (result.resultCode == 0) {
            yield(result)
        } else {
            hasNext = false
            checkRead(result.resultCode)
        }
    }
}