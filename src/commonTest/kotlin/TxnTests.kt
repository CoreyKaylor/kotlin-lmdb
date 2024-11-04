import kotlin.test.*

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

    @Test
    fun `txn put - delete - get returns null`() {
        val key = "test".encodeToByteArray()
        val env = createRandomTestEnv()
        env.use {
            var dbi: Dbi? = null
            env.beginTxn {
                dbi = dbiOpen()
                put(dbi!!, key, key)
                commit()
            }
            env.beginTxn {
                delete(dbi!!, key)
                commit()
            }
            env.beginTxn {
                val result = get(dbi!!, key).toValueByteArray()
                assertNull(result)
            }
        }
    }
}