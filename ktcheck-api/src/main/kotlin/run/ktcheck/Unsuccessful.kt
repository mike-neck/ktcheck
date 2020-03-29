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

  class Composite(tags: Iterable<String>, internal val exceptions: Iterable<Unsuccessful>): Unsuccessful(tags, CompositeException(exceptions)) {

    fun type(): FailureType =
        exceptions.map { it.javaClass.kotlin }.let { list ->
          when {
            list.any { it == ByUnhandledException::class } -> FailureType.UN_HANDLE_ERROR
            list.any { it == ByAssertionFailure::class } -> FailureType.ASSERTION_FAILURE
            list.any { it == ByAbortion::class || it == BySkip::class } -> FailureType.ABORTION
            else -> FailureType.UN_HANDLE_ERROR
          }
        }

    enum class FailureType {
      UN_HANDLE_ERROR,
      ASSERTION_FAILURE,
      ABORTION
    }
  }

  

  class CompositeException(private val exceptions: Iterable<Unsuccessful>): RuntimeException() {
    override val message: String? get() = """|
      |composite exception:
      |  ${exceptions.joinToString("\n  ") { showOneLine(it) }}
    """.trimMargin()
  }

  companion object {
    fun showOneLine(unsuccessful: Unsuccessful, spaceSize: Int = 2, spaces: String = (1..spaceSize).joinToString("") {  " " }): String =
        when (unsuccessful) {
          is Composite -> unsuccessful.exceptions.joinToString("\n${spaces}") { showOneLine(it, 0) }
          is BySkip -> "${unsuccessful.tags.joinToString(", ", spaces)} - skipped"
          is ByAbortion -> "${unsuccessful.tags.joinToString(", ", spaces)} - skipped(aborted)"
          is ByUnhandledException -> "${unsuccessful.tags.joinToString(", ", spaces)} - error: ${unsuccessful.original}"
          is ByAssertionFailure -> "${unsuccessful.tags.joinToString(", ", spaces)} - failed - expected: ${unsuccessful.original.expected.stringRepresentation}/ actual : ${unsuccessful.original.actual.stringRepresentation}"
        }
  }
}
