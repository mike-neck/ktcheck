package org.mikeneck.check

import org.junit.jupiter.api.Test

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
}
