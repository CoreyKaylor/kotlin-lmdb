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
            put(dbi!!, "test".encodeToByteArray(), expected.encodeToByteArray())
            commit()
        }
        env.beginTxn {
            val (_, _, result) = get(dbi!!, "test".encodeToByteArray())
            val value = result.toByteArray()?.decodeToString()
            assertEquals(expected, value)
        }
    }
}