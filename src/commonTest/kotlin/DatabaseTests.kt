import kotlin.test.Test
import kotlin.test.assertEquals

class DatabaseTests {

    @Test
    fun `stats can be read`() {
        val path = pathCreateTestDir()
        val env = Env()
        env.open(path)
        env.beginTxn {
            val dbi = dbiOpen()
            val stat = dbi.stat(this)
            assertEquals(stat.entries, 0u)
        }
    }
}