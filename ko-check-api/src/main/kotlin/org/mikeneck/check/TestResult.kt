package org.mikeneck.check

/**
 * Represents a collection of all check results which the test has.
 */
data class TestResult(
   private val results: Iterable<CheckResult>
) {

  val success: Boolean = results.all { it.success }

  fun successTests(): Iterable<CheckResult> = results.filter { it.success }

  fun failedTests(): Iterable<CheckResult> = results.filter { it.success.not() }
}
