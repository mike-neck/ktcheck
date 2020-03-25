package org.mikeneck.check.assertion

import org.mikeneck.check.Assertion

interface Matcher<T> {
  fun perform(actual: T): Assertion
}

fun <T: Any> containAll(vararg items: T): Matcher<Iterable<T>> = object : Matcher<Iterable<T>> {
  override fun perform(actual: Iterable<T>): Assertion =
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

fun <T: Any> be(expected: T): Matcher<T> = object : Matcher<T> {
  override fun perform(actual: T): Assertion =
      if (actual == expected) Assertion.success()
      else Assertion.fail(expected, actual)
}
