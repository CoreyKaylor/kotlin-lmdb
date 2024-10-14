import kotlin.test.Test
import kotlin.test.assertNotNull

class CursorTests {

    @Test
    fun `cursor can be opened`() {
        val env = createRandomTestEnv()
        env.use {
            env.beginTxn {
                val dbi = dbiOpen()
                val cursor = openCursor(dbi)
                cursor.use {
                    assertNotNull(cursor)
                }
            }
        }
    }
}