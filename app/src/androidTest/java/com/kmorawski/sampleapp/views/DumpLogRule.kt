package com.kmorawski.sampleapp.views

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Substitutes a logger used by the domain module with an in-memory instance
 * and dumps log contents on test failure, allowing easier tracking of what's gone wrong
 *
 * NOTE:
 * -> The logger is recreated after every test, meaning every test keeps its own logging record
 * -> This solution only works for domain module, because since it's independent of Android,
 * the Android logger is abstracted away already (it's normally injected by TigoApplication class
 * at startup)
 * -> The value of BuildConfig.DEBUG is irrelevant for it to work, because domain module can't see
 * it anyway - it "finds out" logging is disabled simply because TigoApplication skips injecting
 * the Android logger. In here, we inject it indiscriminately so tests can be ran or debugged alike.
 */
class DumpLogOnFailureRule(private val logDump: () -> String) : TestRule {
    override fun apply(base: Statement?, description: Description?) = object : Statement() {
        override fun evaluate() {
            evaluate(base)
        }
    }

    private fun evaluate(base: Statement?) {
        val dump = logDump()
        try {
            // actually runs the test
            base?.evaluate()
        } catch (ex: Throwable) {
            // enriching the original message with log contents
            throw AssertionError("${ex.message}\nRecorded log:\n$dump", ex)
        }
    }
}