package org.mikeneck.check

import org.opentest4j.AssertionFailedError
import org.opentest4j.TestAbortedException
import org.opentest4j.TestSkippedException

/**
 * Base class for test failures, such as abort, assertion failure, skipping, exception in given step... etc.
 */
sealed class Unsuccessful(val tags: Iterable<String>, open val original: Throwable): RuntimeException(original) {

  class BySkip(tags: Iterable<String>, override val original: TestSkippedException): Unsuccessful(tags, original)

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
    override fun toString(): String =
        """|
          |Test failed. ${tags.joinToString(",")}
          |expected: ${original.expected.stringRepresentation}
          |actual  : ${original.actual.stringRepresentation}
          |
          |given $givenDescription:  $given
          |when $whenDescription:  $`when`
          |then $thenDescription
        """.trimMargin()
  }
}
