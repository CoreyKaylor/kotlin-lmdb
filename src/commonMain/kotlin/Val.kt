expect class Val {
    fun toByteArray() : ByteArray?
}

expect fun ByteArray.toVal() : Val

