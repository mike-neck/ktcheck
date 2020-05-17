package run.ktcheck.assertion

import run.ktcheck.Assertion

interface Matcher<in T> {
  fun perform(actual: T): Assertion
}

abstract class MatcherSupport<in T>: Matcher<T> {
  override fun perform(actual: T): Assertion =
      if (matches(actual)) Assertion.success()
      else Assertion.fail(expectedValue, actual)

  abstract fun matches(actual: T): Boolean

  abstract val expectedValue: Any
}

object IterableMatchers {

  fun <T: Any, C: Collection<T>> haveSize(size: Int): Matcher<C> = object : MatcherSupport<C>() {
    override fun matches(actual: C): Boolean = actual.size  == size
    override val expectedValue: Any = "has size $size"
  }

  fun <T: Any, C: Iterable<T>> containAll(vararg items: T): Matcher<C> = object : Matcher<C> {
    override fun perform(actual: C): Assertion =
        actual.toSet()
            .let { act ->
              when {
                act.containsAll(items.toList()) -> Assertion.success()
                else ->
                  items.mapIndexed { index, item -> index to item }
                      .filter { pair -> !act.contains(pair.second) }
                      .joinToString(", ", "[", "]") { pair -> "${pair.second}(index:${pair.first})" }
                      .let { Assertion.fail(actual, "contains ${items.toList()}\n,  but $it is not found") }
              } }
  }
}

object AnyMatchers {

  fun <T: Any> be(expected: T): Matcher<T> = object : Matcher<T> {
    override fun perform(actual: T): Assertion =
        if (actual == expected) Assertion.success()
        else Assertion.fail(expected, actual)
  }
}

object StringMatchers {

  fun contain(segment: String): Matcher<String> = object : MatcherSupport<String>() {
    override fun matches(actual: String): Boolean = actual.contains(segment)
    override val expectedValue: Any = "expected to contain $segment"
  }
}
