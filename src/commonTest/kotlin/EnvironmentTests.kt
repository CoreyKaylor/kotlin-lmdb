import kotlin.test.Test

class EnvironmentTests {

    @Test
    fun `can successfully open and close environment`() {
        val envDir = pathCreateTestDir()
        val env = Environment()
        env.open(envDir)
        env.close()
    }
}