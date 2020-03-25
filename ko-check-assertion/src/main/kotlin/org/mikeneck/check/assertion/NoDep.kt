package org.mikeneck.check.assertion

import org.mikeneck.check.Assertion

object NoDep {

  fun <C: Any, G: Any, W: Any> expect(expected: W): C.(G, W) -> Assertion = { _, actual ->
    if (actual == expected) Assertion.success()
    else Assertion.fail(expected, actual)
  }

  fun <C: Any, G: Any, W: Any> expectNull(): C.(G, W?) -> Assertion = { _, actual ->
    if (actual == null) Assertion.success()
    else Assertion.fail(null, actual)
  }

  fun <C: Any, G: Any, W: Any> expectNotNull(): C.(G, W?) -> Assertion = { _, actual ->
    if (actual != null) Assertion.success()
    else Assertion.fail("not null", actual)
  }

  infix fun <T: Any> T.shouldBe(expected: T): Assertion =
      if (this == expected) Assertion.success()
      else Assertion.fail(expected, this)

  infix fun <T: Any> T.shouldNotBe(unexpected: T): Assertion =
      if (this != unexpected) Assertion.success()
      else Assertion.fail("not to be $unexpected", this)

  inline infix fun <T: Any> T.satisfies(predicate: (T) -> Boolean): Assertion =
      if (predicate(this)) Assertion.success()
      else Assertion.fail()

  infix fun <T: Any> T.should(matcher: Matcher<T>): Assertion = matcher.perform(this)

  infix fun <T: Any> Iterable<T>.shouldContain(expected: T): Assertion =
      if (this.any { it == expected }) Assertion.success()
      else Assertion.fail("contains $expected", this)

  infix fun String.shouldContain(expected: CharSequence): Assertion =
      if (this.contains(expected)) Assertion.success()
      else Assertion.fail("contains $expected", this)

  infix fun String.shouldStartWith(expected: CharSequence): Assertion =
      if (this.startsWith(expected)) Assertion.success()
      else Assertion.fail("starts with $expected", this)

  infix fun String.shouldEndWith(expected: CharSequence): Assertion =
      if (this.endsWith(expected)) Assertion.success()
      else Assertion.fail("ends with $expected", this)
}
