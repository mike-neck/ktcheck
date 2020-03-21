package org.mikeneck.check

import org.opentest4j.AssertionFailedError

interface Assertion {

  fun result(checkDescription: CheckDescription, checkContext: CheckContext): Unsuccessful?

  fun toCheckResult(checkDescription: CheckDescription, checkContext: CheckContext): CheckResult

  companion object {

    fun success(): Assertion = Success

    fun fail(expected: Any?, actual: Any?): Assertion = ComparisonFailure(expected, actual)

    fun fail(): Assertion = Failure
  }

  object Success: Assertion {

    override fun result(checkDescription: CheckDescription, checkContext: CheckContext): Unsuccessful? = null

    override fun toCheckResult(checkDescription: CheckDescription, checkContext: CheckContext): CheckResult =
        CheckResult.success(checkDescription, checkContext)
  }

  class ComparisonFailure(private val expected: Any?, private val actual: Any?): Assertion {

    override fun result(checkDescription: CheckDescription, checkContext: CheckContext): Unsuccessful = Unsuccessful.ByAssertionFailure(
        given = checkContext.given,
        givenDescription = checkDescription.givenDescription,
        `when` = checkContext.`when`,
        whenDescription = checkDescription.whenDescription,
        thenDescription = checkDescription.thenDescription,
        tags = listOf(checkDescription.id, checkDescription.name),
        original = AssertionFailedError("test failed - ${checkDescription.name}", expected, actual))

    override fun toCheckResult(checkDescription: CheckDescription, checkContext: CheckContext): CheckResult =
        CheckResult.failure(checkDescription,checkContext, result(checkDescription, checkContext))
  }

  object Failure: Assertion {

    override fun result(checkDescription: CheckDescription, checkContext: CheckContext): Unsuccessful = Unsuccessful.ByAssertionFailure(
        given = checkContext.given,
        givenDescription = checkDescription.givenDescription,
        `when` = checkContext.`when`,
        whenDescription = checkDescription.whenDescription,
        thenDescription = checkDescription.thenDescription,
        tags = listOf(checkDescription.id, checkDescription.name),
        original = AssertionFailedError("test failed - ${checkDescription.name}")
    )

    override fun toCheckResult(checkDescription: CheckDescription, checkContext: CheckContext): CheckResult =
        CheckResult.failure(checkDescription, checkContext, result(checkDescription, checkContext))
  }
}
