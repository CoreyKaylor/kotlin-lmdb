import okio.FileSystem
import okio.Path.Companion.toPath

fun pathCreateTestDir() : String {
    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    val fs = getFileSystem()
    val directoryPath = "./build".toPath()
    var testPath = fs.canonicalize(directoryPath) / "lmdb-envs"
    if (!fs.exists(testPath)) {
        fs.createDirectory(testPath, true)
    }
    testPath /= getRandomString(10)
    if (!fs.exists(testPath)) {
        fs.createDirectory(testPath, true)
    }
    return testPath.toString()
}

expect fun getFileSystem(): FileSystem