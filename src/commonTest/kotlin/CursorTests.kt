import kotlin.test.Test
import kotlin.test.assertNotNull

class CursorTests {

    @Test
    fun `cursor can be opened`() {
        val path = pathCreateTestDir()
        val env = Env()
        env.open(path)
        env.beginTxn {
            val dbi = dbiOpen()
            val cursor = openCursor(dbi)
            assertNotNull(cursor)
            cursor.close()
        }
    }
}