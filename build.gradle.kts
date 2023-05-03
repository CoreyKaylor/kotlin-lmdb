plugins {
    kotlin("multiplatform") version "1.8.20"
}

group = "com.github.kotlin-lmdb"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {

        hostOs == "Mac OS X" -> macosArm64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }.apply {
        compilations.getByName("main") {
            cinterops {
                val liblmdb by creating {
                    includeDirs.allHeaders("/usr/local/include")
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.squareup.okio:okio:3.3.0")
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
                optIn("kotlin.RequiresOptIn")
                optIn("kotlin.ExperimentalStdlibApi")
            }
        }
    }
}
tasks.withType<Test> {
    this.testLogging {
        this.showStandardStreams = true
    }
}