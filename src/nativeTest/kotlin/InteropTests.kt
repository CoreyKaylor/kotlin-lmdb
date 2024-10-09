import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlin.test.Test
import kotlin.test.assertEquals

class InteropTests {

    @Test
    fun `can pin and retrieve values from MDB_val`() {
        val original = "original value"
        memScoped {
            val mdbVal = withMDB_val(original.encodeToByteArray()) { value -> value }
            val newValue = mdbVal.ptr.pointed.toByteArray()
            assertEquals(original, newValue?.toKString())
        }
    }
}