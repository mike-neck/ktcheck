package org.mikeneck.check.assertion

import org.mikeneck.check.Assertion
import org.mikeneck.check.CheckContext
import org.mikeneck.check.CheckDescription
import org.mikeneck.check.Given
import org.mikeneck.check.Test
import org.mikeneck.check.Timer

import org.mikeneck.check.assertion.NoDep.expect
import org.mikeneck.check.assertion.NoDep.expectNull
import org.mikeneck.check.assertion.NoDep.shouldBe
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

object NoDepExpectNullTest: Test
by Given("expectNull", { expectNull<Unit, Unit, String>() })
    .When("assert non-null with it", { assertion -> Unit.assertion(Unit, "foo") })
    .Then("fail", { _, assertion ->
      if (assertion.result(checkDescription, checkContext("not-null", "not-null")) != null) Assertion.success()
      else Assertion.fail()
    })
    .When("assert null with it",{ assertion -> Unit.assertion(Unit, null) })
    .Then("success", { _, assertion ->
      if (assertion.result(checkDescription, checkContext("not-null", "not-null")) == null) Assertion.success()
      else Assertion.fail()
    })

object NoDepShouldBeTest: Test
by Given("1", { 1 })
    .When("assert with 'shouldBe' 1",{ one -> one shouldBe 1 })
    .Then("it is success", { _, assertion -> 
      if (assertion.result(checkDescription, checkContext(1, 1)) == null) Assertion.success()
      else Assertion.fail()
    })
    .When("assert with 'shouldBe 2'", { one -> one shouldBe 2 })
    .Then("it is failure", { _, assertion ->
      if (assertion.result(checkDescription, checkContext(1, 2)) != null) Assertion.success()
      else Assertion.fail()
    })
