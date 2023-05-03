import kotlin.test.Test

class EnvTests {

    @Test
    fun `env open - close`() {
        val envDir = pathCreateTestDir()
        val env = Env()
        env.open(envDir)
        env.close()
    }
}