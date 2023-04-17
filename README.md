kotlin-lmdb
=============

Kotlin multi-platform library for OpenLDAP's LMDB key-value store.

This is not a wrapper around other libraries. Kotlin-Native uses cinterop
and JVM uses JNR-FFI for communicating directly with LMDB native lib.

It's very early still, but the foundation is laid for a good consistent API
that starts very early with multi-platform considerations out of the way.

```kotlin
val envDir = "path_to_directory_for_data"
val env = Environment()
env.open(envDir)
// More to come
env.close()
```

<a href="http://lmdb.tech/doc" target="_blank">Official LMDB API docs</a>