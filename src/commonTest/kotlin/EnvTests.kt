import kotlin.test.Test
import kotlin.test.assertFailsWith

class EnvTests {

    @Test
    fun `env open - close`() {
        val env = createRandomTestEnv()
        env.close()
    }

    @Test
    fun `start transaction before opening environment`() {
        val env = createRandomTestEnv(open = false)
        assertFailsWith(LmdbException::class) {
            env.beginTxn()
        }
    }
}