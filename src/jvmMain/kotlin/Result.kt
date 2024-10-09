actual data class Result(actual val resultCode: Int, val key: MDBVal?, val data: MDBVal?)

actual fun Result.toKeyByteArray() : ByteArray? = this.key!!.toByteArray()
actual fun Result.toDataByteArray() : ByteArray? = this.data!!.toByteArray()
