import kotlinx.io.files.*
import kotlin.uuid.Uuid

fun pathCreateTestDir() : String {
    val fs = SystemFileSystem
    val testPath = Path(SystemTemporaryDirectory.toString(), Uuid.random().toString())
    if (!fs.exists(testPath)) {
        fs.createDirectories(testPath, true)
    }
    return testPath.toString()
}

fun createRandomTestEnv(open: Boolean = true, mapSize: ULong? = null, maxDatabases: UInt? = null) : Env {
    val path = pathCreateTestDir()
    val env = Env()
    if(mapSize != null) {
        env.mapSize = mapSize
    }
    if(maxDatabases != null) {
        env.maxDatabases = maxDatabases
    }
    if (open) {
        env.open(path)
    }
    return env
}