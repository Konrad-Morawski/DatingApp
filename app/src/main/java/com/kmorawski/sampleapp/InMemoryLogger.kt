package com.kmorawski.sampleapp

/**
 * Logger substitute that stores all messages in-memory.
 * For the purpose of debugging unit tests.
 */
class InMemoryLogger : Logger {
    private val log = StringBuilder()
    private var lineNumber = 1

    val contents
        get() = log.toString()

    override fun debug(message: String) {
        log.appendln("($lineNumber) $message")
        lineNumber++
    }

    fun clear() {
        log.clear()
        lineNumber = 1
    }
}