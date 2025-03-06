kotlin-lmdb
=============
[![Build and Test](https://github.com/CoreyKaylor/kotlin-lmdb/actions/workflows/build.yml/badge.svg)](https://github.com/CoreyKaylor/kotlin-lmdb/actions/workflows/build.yml)

Kotlin multi-platform library for OpenLDAP's LMDB key-value store.

This is not a wrapper around other libraries. Kotlin-Native uses cinterop
and JVM uses JNR-FFI for communicating directly with LMDB native lib.

It's very early still, but the foundation is laid for a good consistent API
that starts very early with multi-platform considerations out of the way.

```kotlin
val envDir = "path_to_directory_for_data"
val env = Env()
env.open(envDir)
env.beginTxn { tx ->
    val dbi = tx.dbiOpen()
    tx.put(dbi, "test".encodeToByteArray(), "value".encodeToByteArray())
    tx.commit()
}
env.close()
```

<a href="http://lmdb.tech/doc" target="_blank">Official LMDB API docs</a>