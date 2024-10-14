import kotlinx.io.files.*

fun pathCreateTestDir() : String {
    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    val fs = SystemFileSystem
    val directoryPath = Path("./build", "lmdb-envs")
    if (!fs.exists(directoryPath)) {
        fs.createDirectories(directoryPath, true)
    }
    val testPath = Path(directoryPath.toString(), getRandomString(10))
    if (!fs.exists(testPath)) {
        fs.createDirectories(testPath, true)
    }
    return testPath.toString()
}

fun createRandomTestEnv(open: Boolean = true) : Env {
    val path = pathCreateTestDir()
    val env = Env()
    if (open) {
        env.open(path)
    }
    return env
}