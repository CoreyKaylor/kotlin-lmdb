import kotlin.test.Test
import kotlin.test.assertEquals

class DatabaseTests {

    @Test
    fun `stats can be read`() {
        val env = createRandomTestEnv()
        env.use {
            env.beginTxn {
                val dbi = dbiOpen()
                val stat = dbi.stat(this)
                assertEquals(stat.entries, 0u)
            }
        }
    }
}