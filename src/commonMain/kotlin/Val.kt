expect class Val {
    fun toByteArray() : ByteArray?
}

expect fun ByteArray.toVal() : Val

fun Triple<Int, Val, Val>.toValueByteArray() =
    if(this.first == 0) {
       this.third.toByteArray()
    } else {
        null
    }

fun Triple<Int, Val, Val>.toKeyByteArray() =
    if(this.first == 0) {
        this.second.toByteArray()
    } else {
        null
    }

