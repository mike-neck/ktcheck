package org.mikeneck.check

import org.opentest4j.AssertionFailedError

interface Assertion {

  fun result(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext): Unsuccessful?

  fun toCheckResult(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext): CheckResult

  companion object {

    fun success(): Assertion = Success

    fun fail(expected: Any?, actual: Any?): Assertion = ComparisonFailure(expected, actual)

    fun fail(): Assertion = Failure
  }

  object Success: Assertion {

    override fun result(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext): Unsuccessful? = null

    override fun toCheckResult(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext): CheckResult =
        CheckResult.success(ktPropertyDescription, ktPropertyContext)
  }

  class ComparisonFailure(private val expected: Any?, private val actual: Any?): Assertion {

    override fun result(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext): Unsuccessful = Unsuccessful.ByAssertionFailure(
        given = ktPropertyContext.given,
        givenDescription = ktPropertyDescription.givenDescription,
        `when` = ktPropertyContext.`when`,
        whenDescription = ktPropertyDescription.whenDescription,
        thenDescription = ktPropertyDescription.thenDescription,
        tags = listOf(ktPropertyDescription.id, ktPropertyDescription.name),
        original = AssertionFailedError("test failed - ${ktPropertyDescription.name}", expected, actual))

    override fun toCheckResult(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext): CheckResult =
        CheckResult.failure(ktPropertyDescription,ktPropertyContext, result(ktPropertyDescription, ktPropertyContext))
  }

  object Failure: Assertion {

    override fun result(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext): Unsuccessful = Unsuccessful.ByAssertionFailure(
        given = ktPropertyContext.given,
        givenDescription = ktPropertyDescription.givenDescription,
        `when` = ktPropertyContext.`when`,
        whenDescription = ktPropertyDescription.whenDescription,
        thenDescription = ktPropertyDescription.thenDescription,
        tags = listOf(ktPropertyDescription.id, ktPropertyDescription.name),
        original = AssertionFailedError("test failed - ${ktPropertyDescription.name}")
    )

    override fun toCheckResult(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext): CheckResult =
        CheckResult.failure(ktPropertyDescription, ktPropertyContext, result(ktPropertyDescription, ktPropertyContext))
  }
}
