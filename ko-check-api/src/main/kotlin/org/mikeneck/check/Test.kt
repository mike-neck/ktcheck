package org.mikeneck.check

/**
 * Represents a test, which contains all checks for a subject.
 */
interface Test {

  /**
   * Returns all checks.
   */
  val all: Iterable<Check>

  /**
   * Performs all checks.
   */
  fun performAll(): TestResult = TestResult(all.map { it.perform() })

  /**
   * Run all checks in standalone environment.
   */
  fun runStandalone(): Unit = performAll().throwOnAnyFailure()
}
