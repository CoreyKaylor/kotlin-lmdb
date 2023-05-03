import kotlin.test.Test
import kotlin.test.assertEquals

class TxnTests {

    @Test
    fun `txn put - get`() {
        val expected = "value"
        val path = pathCreateTestDir()
        val env = Env()
        env.open(path)
        env.beginTxn { tx ->
            val dbi = tx.dbiOpen()
            tx.put(dbi, "test".encodeToByteArray(), expected.encodeToByteArray())
            val result = tx.get(dbi, "test".encodeToByteArray())
            val value = result.toDataByteArray().decodeToString()
            assertEquals(expected, value)
        }
    }
}