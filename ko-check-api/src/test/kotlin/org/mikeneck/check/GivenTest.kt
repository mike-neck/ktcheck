package org.mikeneck.check

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class GivenTest {

  @Test
  fun success(): Unit =
      Given("1") { 1 }
          .When(" + 4") { one -> one + 4 }
          .Then(" = 5") { _, actual -> if (actual == 5) Assertion.success() else Assertion.fail(5, actual) }
          .performAll()
          .results allSatisfies { "$it / ${it.unsuccessful}" to it.success }

  @Test
  fun failure(): Unit =
      Given("[foo, bar, baz, qux, quux]") { listOf("foo", "bar", "baz", "qux", "quux") }
          .When("take from index 1 with 3 count") { it.subList(1, 3) }
          .Then(" -> [bar, baz, qux]") { _, actual ->
            if (actual == listOf("bar", "baz", "qux")) Assertion.success() 
            else Assertion.fail(listOf("bar", "baz", "qux"), actual) 
          }.performAll()
          .success shouldBe false

  @Test
  fun error(): Unit =
      Given("emptyList") { emptyList<String>() }
          .When("take item at index 3") { it[3] }
          .Then("this code does not executed") { _, _ -> Assertion.success() }
          .performAll()
          .results all Assert { it.unsuccessful shouldBeInstanceOf Unsuccessful.ByUnhandledException::class }

  @Test
  fun allSuccess(): Unit =
      assertDoesNotThrow {
        Given("list [foo, bar, baz, qux, quux]") { listOf("foo", "bar", "baz", "qux", "quux") }
            .When("take its size") { it.size }
            .Then("become 5", expect(5))
            .When("reversed") { it.reversed() }
            .Then("becomes [quux, qux, baz, bar, foo]", expect(listOf("quux", "qux", "baz", "bar", "foo")))
            .runStandalone()
      }

  @Test
  fun someTestsFail(): Unit =
      assertThrows<Throwable>("Unsuccessful.CompositeException") {
        Given("list [foo, bar, baz, qux, quux]") { listOf("foo", "bar", "baz", "qux", "quux") }
            .When("take its size") { it.size }
            .Then("become 5", expect(5))
            .When("reversed") { it.reversed() }
            .Then("becomes [quux, qux, baz, bar, foo]", expect(listOf("quux", "qux", "baz", "bar", "foo")))
            .When("take from index 1 with 3 count") { it.subList(1, 3) }
            .Then(" -> [bar, baz, qux]", expect(listOf("bar", "baz", "qux")))
            .runStandalone()
      } shouldBeInstanceOf Unsuccessful.CompositeException::class

  companion object {
    fun <C: Any,G: Any, T> expect(t: T): C.(G, T) -> Assertion = { _, actual ->
      if (actual == t) Assertion.success()
      else Assertion.fail(t, actual)
    } 
  }
}
