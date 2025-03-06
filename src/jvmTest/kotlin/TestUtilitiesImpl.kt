/**
 * JVM implementation of custom comparer utility functions
 */

/**
 * Register a custom comparer using the JVM implementation
 */
actual fun registerCustomComparer(slot: ValComparer, compareFunction: ValCompare) {
    ValComparerRegistry.registerCustomComparer(slot, compareFunction)
}

/**
 * Clear all custom comparers using the JVM implementation
 */
actual fun clearCustomComparers() {
    ValComparerRegistry.clearCustomComparers()
}