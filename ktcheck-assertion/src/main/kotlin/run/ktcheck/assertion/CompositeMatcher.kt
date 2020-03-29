package run.ktcheck.assertion

import run.ktcheck.Assertion
import run.ktcheck.CheckResult
import run.ktcheck.KtPropertyContext
import run.ktcheck.KtPropertyDescription
import run.ktcheck.Unsuccessful

interface CompositeMatcher<T>: Matcher<T> {

  val matchers: List<Matcher<T>>

  companion object {
    fun <T> all(vararg matchers: Matcher<T>): CompositeMatcher<T> = CompositeMatcherImpl(listOf(*matchers))

    infix fun <T> Matcher<T>.and(matcher: Matcher<T>): CompositeMatcher<T> =
        when (this) {
          is CompositeMatcher<T> -> when (matcher) {
            is CompositeMatcher<T> -> CompositeMatcherImpl(this.matchers + matcher.matchers)
            else -> CompositeMatcherImpl(this.matchers + matcher)
          }
          else -> when (matcher) {
            is CompositeMatcher<T> -> CompositeMatcherImpl(listOf(this) + matcher.matchers)
            else -> CompositeMatcherImpl(listOf(this, matcher))
          }
        }
  }
}

internal class CompositeMatcherImpl<T>(
    override val matchers: List<Matcher<T>>
): CompositeMatcher<T> {

  override fun perform(actual: T): Assertion =
      CompositeAssertion(matchers.map { it.perform(actual) })
} 

internal class CompositeAssertion(
    private val assertions: Collection<Assertion>
): Assertion {

  override fun result(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext): Unsuccessful? =
      unsuccessful(ktPropertyDescription, ktPropertyContext)

  private fun unsuccessful(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext): Unsuccessful.Composite? =
      when (val unsuccessful = assertions.mapNotNull { it.result(ktPropertyDescription, ktPropertyContext) }) {
        emptyList<Unsuccessful>() -> null
        else -> Unsuccessful.Composite(listOf(ktPropertyDescription.id, "assertion for all", ktPropertyDescription.name), unsuccessful)
      }

  override fun toCheckResult(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext): CheckResult =
      when (assertions.isEmpty()) {
        true -> CheckResult.success(ktPropertyDescription, ktPropertyContext)
        false -> when (val unsuccessful = this.unsuccessful(ktPropertyDescription, ktPropertyContext)) {
          null -> CheckResult.success(ktPropertyDescription, ktPropertyContext)
          else -> unsuccessful.toCheckResult(ktPropertyDescription, ktPropertyContext)
        }
      }

  fun Unsuccessful.Composite.toCheckResult(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext): CheckResult =
      when (this.type()) {
        Unsuccessful.Composite.FailureType.UN_HANDLE_ERROR -> CheckResult.error(ktPropertyDescription, ktPropertyContext.timer, this)
        Unsuccessful.Composite.FailureType.ASSERTION_FAILURE -> CheckResult.failure(ktPropertyDescription, ktPropertyContext, this)
        Unsuccessful.Composite.FailureType.ABORTION -> CheckResult.skip(ktPropertyDescription, ktPropertyContext.timer, this)
      }
}
