package run.ktcheck

import org.opentest4j.AssertionFailedError
import org.opentest4j.TestAbortedException
import org.opentest4j.TestSkippedException

/**
 * Base class for test failures, such as abort, assertion failure, skipping, exception in given step... etc.
 */
sealed class Unsuccessful(val tags: Iterable<String>, open val original: Throwable): RuntimeException(original) {

  class BySkip(tags: Iterable<String>, override val original: TestSkippedException): Unsuccessful(tags, original) {
    override fun toString(): String = "Skipped [${tags.joinToString(", ")}] - ${original.message}"
  }

  class ByAbortion(tags: Iterable<String>, override val original: TestAbortedException): Unsuccessful(tags, original)

  class ByUnhandledException(tags: Iterable<String>, original: Throwable): Unsuccessful(tags, original)

  class ByAssertionFailure(
      tags: Iterable<String>,
      private val givenDescription: String,
      private val given: Any,
      private val whenDescription: String,
      private val `when`: Any?,
      private val thenDescription: String,
      override val original: AssertionFailedError): Unsuccessful(tags, original) {

    private val noDetailValues: Boolean get() =
        when (original.expected to original.actual) {
          null to null -> true
          else -> false
        }

    private val comparison: String get() = 
      if (noDetailValues) "[failure without comparison]\n"
      else """|
        |expected: ${original.expected?.stringRepresentation ?: "<null>"}
        |actual  : ${original.actual?.stringRepresentation ?: "<null>"}
        |
      """.trimMargin()

    override fun toString(): String =
        """|
          |Test failed. ${tags.joinToString(",")}
          |${comparison}
          |Given $givenDescription:  $given
          |When $whenDescription:  $`when`
          |Then $thenDescription
          |
        """.trimMargin()
  }

  class CompositeException(private val exceptions: Iterable<Unsuccessful>): RuntimeException() {
    override val message: String? get() = """|
      |composite exception:
      |  ${exceptions.map { 
      when (it) {
        is Unsuccessful.BySkip -> "${it.tags.joinToString(", ")} - skipped"
        is Unsuccessful.ByAbortion -> "${it.tags.joinToString(", ")} - skipped(aborted)"
        is Unsuccessful.ByUnhandledException -> "${it.tags.joinToString(", ")} - error: ${it.original}"
        is Unsuccessful.ByAssertionFailure -> "${it.tags.joinToString(", ")} - failed - expected: ${it.original.expected.stringRepresentation}/ actual : ${it.original.actual.stringRepresentation}"
      } }.joinToString("\n  ")}
    """.trimMargin()
  }
}
