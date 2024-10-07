import kotlin.test.Test
import kotlin.test.assertEquals

class TxnTests {

    @Test
    fun `txn put - get`() {
        val expected = "value"
        val path = pathCreateTestDir()
        val env = Env()
        env.open(path)
        var dbi: Dbi? = null
        env.beginTxn {
            dbi = dbiOpen()
            println("Original size: ${expected.encodeToByteArray().size}")
            put(dbi!!, "test".encodeToByteArray(), expected.encodeToByteArray())
            commit()
        }
        env.beginTxn {
            val result = get(dbi!!, "test".encodeToByteArray())
            val value = result.toDataByteArray().decodeToString()
            assertEquals(expected, value)
        }
    }
}