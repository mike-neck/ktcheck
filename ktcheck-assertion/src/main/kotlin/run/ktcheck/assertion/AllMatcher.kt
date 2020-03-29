package run.ktcheck.assertion

import run.ktcheck.Assertion
import run.ktcheck.CheckResult
import run.ktcheck.KtPropertyContext
import run.ktcheck.KtPropertyDescription
import run.ktcheck.Unsuccessful

interface AllMatcher<T>: Matcher<T> {

  val matchers: List<Matcher<T>>

  companion object {
    fun <T> all(vararg matchers: Matcher<T>): AllMatcher<T> = AllMatcherImpl(listOf(*matchers))

    infix fun <T> Matcher<T>.and(matcher: Matcher<T>): AllMatcher<T> =
        when (this) {
          is AllMatcher<T> -> when (matcher) {
            is AllMatcher<T> -> AllMatcherImpl(this.matchers + matcher.matchers)
            else -> AllMatcherImpl(this.matchers + matcher)
          }
          else -> when (matcher) {
            is AllMatcher<T> -> AllMatcherImpl(listOf(this) + matcher.matchers)
            else -> AllMatcherImpl(listOf(this, matcher))
          }
        }
  }
}

internal class AllMatcherImpl<T>(
    override val matchers: List<Matcher<T>>
): AllMatcher<T> {

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
