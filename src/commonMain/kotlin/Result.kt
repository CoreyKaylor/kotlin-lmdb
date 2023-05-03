expect class Result {
    val resultCode: Int
}

expect fun Result.toKeyByteArray() : ByteArray
expect fun Result.toDataByteArray() : ByteArray