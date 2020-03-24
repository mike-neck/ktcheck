package org.mikeneck.check.assertion

import org.mikeneck.check.Assertion
import org.mikeneck.check.CheckContext
import org.mikeneck.check.CheckDescription
import org.mikeneck.check.Given
import org.mikeneck.check.Test
import org.mikeneck.check.Timer

import org.mikeneck.check.assertion.NoDep.expect
import java.time.Instant

val checkDescription: CheckDescription = object : CheckDescription {
  override val id: String get() = "id"
  override val givenDescription: String get() = "description"
  override val whenDescription: String get() = "when"
  override val thenDescription: String get() = "then"
}

fun checkContext(given: Any, `when`: Any?, instant: Instant = Instant.now()): CheckContext = object : CheckContext {
  override val timer: Timer
    get() = Timer(instant)
  override val given: Any get() = given
  override val `when`: Any? get() = `when`
}

object NoDepExpectTest: Test
by Given("expect 1", { expect<Unit, Unit, Int>(1) })
    .When("assert 1 with it", { assertion -> Unit.assertion(Unit, 1) })
    .Then("success", { _, assertion: Assertion ->
      if (assertion.result(checkDescription, checkContext(1, 1)) == null) Assertion.success() 
      else Assertion.fail()
    })
    .When("assert 2 with it", { assertion -> Unit.assertion(Unit, 2) })
    .Then("failure", { _, assertion -> 
      if (assertion.result(checkDescription, checkContext(1, 2)) != null) Assertion.success()
      else Assertion.fail()
    })


