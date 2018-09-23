package com.kmorawski.sampleapp

/**
 * Convenience function for asserting correct values of integer resources
 */
fun Int.requireAtLeast(minimum: Int, name: () -> String): Int {
    require(this >= minimum) {
        "Critical assertion error: ${name()} should have been at least $minimum. Was: $this. Review the implementation."
    }
    return this
}