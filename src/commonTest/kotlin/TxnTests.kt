import kotlin.test.Test
import kotlin.test.assertEquals

class TxnTests {

    @Test
    fun `txn put - get`() {
        val expected = "value"
        val key = "test".encodeToByteArray()
        val env = createRandomTestEnv()
        env.use {
            var dbi: Dbi? = null
            env.beginTxn {
                dbi = dbiOpen()
                put(dbi!!, key, expected.encodeToByteArray())
                commit()
            }
            env.beginTxn {
                val (_, _, result) = get(dbi!!, key)
                val value = result.toByteArray()?.decodeToString()
                assertEquals(expected, value)
            }
        }
    }
}