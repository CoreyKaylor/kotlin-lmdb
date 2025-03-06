import kotlinx.cinterop.*
import lmdb.*

/**
 * Native implementation of custom comparer utility functions
 */

// Map to store custom compare functions
private val customCompareFunctions = mutableMapOf<ValComparer, ValCompare>()

/**
 * Register a custom comparer using the Native implementation
 * This registers both a Kotlin function for API consistency and a native function
 * that calls into the appropriate top-level function based on the slot
 */
actual fun registerCustomComparer(slot: ValComparer, compareFunction: ValCompare) {
    // Store the Kotlin function for later use by the native callback
    customCompareFunctions[slot] = compareFunction
    
    // Register for API consistency
    ValComparerRegistry.registerCustomComparer(slot, compareFunction)
    
    // Register the appropriate native callback based on the slot
    val nativeComparer = when (slot) {
        ValComparer.CUSTOM_1 -> staticCFunction(::custom1ComparerNative)
        ValComparer.CUSTOM_2 -> staticCFunction(::custom2ComparerNative)
        ValComparer.CUSTOM_3 -> staticCFunction(::custom3ComparerNative)
        ValComparer.CUSTOM_4 -> staticCFunction(::custom4ComparerNative)
        else -> throw IllegalArgumentException("Only CUSTOM_1 through CUSTOM_4 supported")
    }
    
    // Register the native C function pointer
    ValComparerRegistry.registerNativeCustomComparer(slot, nativeComparer)
}

/**
 * Clear all custom comparers using the Native implementation
 */
actual fun clearCustomComparers() {
    customCompareFunctions.clear()
    ValComparerRegistry.clearCustomComparers()
}

// Top-level functions for each custom slot that don't capture any variables
// These are used with staticCFunction and delegate to the stored compare functions

fun custom1ComparerNative(left: CPointer<MDB_val>?, right: CPointer<MDB_val>?): Int {
    val compareFunction = customCompareFunctions[ValComparer.CUSTOM_1] 
        ?: return 0 // Default to 0 if not registered
    
    val a = Val.forCompare(left)
    val b = Val.forCompare(right)
    return compareFunction(a, b)
}

fun custom2ComparerNative(left: CPointer<MDB_val>?, right: CPointer<MDB_val>?): Int {
    val compareFunction = customCompareFunctions[ValComparer.CUSTOM_2] 
        ?: return 0 // Default to 0 if not registered
    
    val a = Val.forCompare(left)
    val b = Val.forCompare(right)
    return compareFunction(a, b)
}

fun custom3ComparerNative(left: CPointer<MDB_val>?, right: CPointer<MDB_val>?): Int {
    val compareFunction = customCompareFunctions[ValComparer.CUSTOM_3] 
        ?: return 0 // Default to 0 if not registered
    
    val a = Val.forCompare(left)
    val b = Val.forCompare(right)
    return compareFunction(a, b)
}

fun custom4ComparerNative(left: CPointer<MDB_val>?, right: CPointer<MDB_val>?): Int {
    val compareFunction = customCompareFunctions[ValComparer.CUSTOM_4] 
        ?: return 0 // Default to 0 if not registered
    
    val a = Val.forCompare(left)
    val b = Val.forCompare(right)
    return compareFunction(a, b)
}