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

fun <C: Any, A: Any, G: Any, W> successCase(given: G, `when`: W): C.(A, Assertion) -> Assertion = { _, assertion ->
  if (assertion.result(checkDescription, checkContext(given, `when`)) == null) Assertion.success()
  else Assertion.fail()
}

fun <C: Any, A: Any, G: Any, W> failCase(given: G, `when`: W): C.(A, Assertion) -> Assertion = { _, assertion ->
  if (assertion.result(checkDescription, checkContext(given, `when`)) != null) Assertion.success()
  else Assertion.fail()
}

object NoDepExpectTest: Test
by Given("expect 1", { expect<Unit, Unit, Int>(1) })
    .When("assert 1 with it", { assertion -> Unit.assertion(Unit, 1) })
    .Then("success", successCase(1, 1))
    .When("assert 2 with it", { assertion -> Unit.assertion(Unit, 2) })
    .Then("failure", failCase(1, 2))

object NoDepExpectNullTest: Test
by Given("expectNull", { expectNull<Unit, Unit, String>() })
    .When("assert non-null with it", { assertion -> Unit.assertion(Unit, "foo") })
    .Then("fail", failCase("not-null", "not-null"))
    .When("assert null with it",{ assertion -> Unit.assertion(Unit, null) })
    .Then("success", successCase("not-null", null))

object NoDepShouldBeTest: Test
by Given("1", { 1 })
    .When("assert with 'shouldBe' 1",{ one -> one shouldBe 1 })
    .Then("it is success", successCase(1, 1))
    .When("assert with 'shouldBe 2'", { one -> one shouldBe 2 })
    .Then("it is failure", failCase(1, 2))
