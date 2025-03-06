plugins {
    kotlin("multiplatform") version "2.1.10"
}

group = "com.github.kotlin-lmdb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlinx/dev")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    jvmToolchain(21)
    jvm {
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val hostArch = System.getProperty("os.arch")
    val isMacosArm64 = hostOs == "Mac OS X" && hostArch == "aarch64"
    val isMacosX64 = hostOs == "Mac OS X" && !isMacosArm64
    
    when {
        isMacosArm64 -> macosArm64("native")
        isMacosX64 -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }.apply {
        compilations.getByName("main") {
            cinterops {
                val liblmdb by creating {
                    defFile(project.file("src/nativeInterop/cinterop/liblmdb.def"))
                    includeDirs("src/nativeInterop/cinterop/c/")
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.5.4")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.github.jnr:jnr-constants:0.10.4")
                implementation("com.github.jnr:jnr-ffi:2.2.13")
            }
        }
        val jvmTest by getting

        val nativeMain by getting
        val nativeTest by getting

        all {
            languageSettings.apply {
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
                optIn("kotlinx.io.core.ExperimentalIO")
                optIn("kotlin.uuid.ExperimentalUuidApi")
                optIn("kotlin.experimental.ExperimentalNativeApi")
            }
        }
    }
}
tasks.withType<Test> {
    this.testLogging {
        this.showStandardStreams = true
    }
}