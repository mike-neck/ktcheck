package run.ktcheck

/**
 * Represents a test, which contains all checks for a subject.
 */
interface KtCheck {

  /**
   * Returns all checks.
   */
  val all: Iterable<KtProperty>

  /**
   * Performs all checks.
   */
  fun performAll(): TestResult = TestResult(all.map { it.perform() })

  /**
   * Run all checks in standalone environment.
   */
  fun runStandalone(): Unit = performAll().throwOnAnyFailure()
}
