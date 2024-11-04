import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

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

    @Test
    fun `can read environment information`() {
        val mapSize: ULong = (1024 * 1024 * 200).toULong()
        val env = createRandomTestEnv(mapSize = mapSize)
        assertEquals(env.info!!.mapSize, env.mapSize)
    }

    @Test
    fun `can set max databases`() {
        val databaseCount = 2u
        val env = createRandomTestEnv(maxDatabases = databaseCount)
        env.beginTxn {
            dbiOpen("db1", DbiOption.Create)
            dbiOpen("db2", DbiOption.Create)
            commit()
        }
        assertEquals(databaseCount, env.maxDatabases)
    }

    @Test
    fun `can read environment stats`() {
        val env = createRandomTestEnv()
        assertNotNull(env.stat)
    }

    @Test
    fun `can open a readonly environment`() {
        val path = pathCreateTestDir()
        Env().use {
            it.open(path) //must be initially created first before readonly can be opened
        }
        Env().use {
            it.open(path, EnvOption.ReadOnly)
        }
    }
}