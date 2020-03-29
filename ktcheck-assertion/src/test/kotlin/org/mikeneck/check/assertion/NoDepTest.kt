package org.mikeneck.check.assertion

import org.mikeneck.check.Given
import org.mikeneck.check.KtCheck
import org.mikeneck.check.assertion.NoDep.expect
import org.mikeneck.check.assertion.NoDep.expectNotNull
import org.mikeneck.check.assertion.NoDep.expectNull
import org.mikeneck.check.assertion.NoDep.satisfies
import org.mikeneck.check.assertion.NoDep.shouldBe
import org.mikeneck.check.assertion.NoDep.shouldNotBe

object NoDepExpectTest: KtCheck
by Given("expect 1", { expect<Unit, Unit, Int>(1) })
    .When("assert 1 with it", { assertion -> Unit.assertion(Unit, 1) })
    .Then("success", successCase(1, 1))
    .When("assert 2 with it", { assertion -> Unit.assertion(Unit, 2) })
    .Then("failure", failCase(1, 2))

object NoDepExpectNullTest: KtCheck
by Given("expectNull", { expectNull<Unit, Unit, String>() })
    .When("assert non-null with it", { assertion -> Unit.assertion(Unit, "foo") })
    .Then("fail", failCase("not-null", "not-null"))
    .When("assert null with it",{ assertion -> Unit.assertion(Unit, null) })
    .Then("success", successCase("not-null", null))

object NoDepExpectNotNullTest: KtCheck
by Given("expectNotNull", { expectNotNull<Unit, Unit, String>() })
    .When("assert non-null with it", { assertion -> Unit.assertion(Unit, "foo") })
    .Then("success", successCase("non-null", "foo"))
    .When("assert null with it", { assertion -> Unit.assertion(Unit, null) })
    .Then("failure", failCase("non-null", null))

object NoDepShouldBeTest: KtCheck
by Given("1", { 1 })
    .When("assert with 'shouldBe' 1",{ one -> one shouldBe 1 })
    .Then("it is success", successCase(1, 1))
    .When("assert with 'shouldBe 2'", { one -> one shouldBe 2 })
    .Then("it is failure", failCase(1, 2))

object NoDepShouldNotBeTest: KtCheck
by Given("1", { 1 })
    .When("assert it with 'shouldNotBe' 2", { one -> one shouldNotBe 2 })
    .Then("success", successCase(1, 2))
    .When("assert it with 'shouldNotBe' 1", { one -> one shouldNotBe 1 })
    .Then("failure", failCase(1, 1))

object NoDeoSatisfiesTest: KtCheck
by Given("1", { 1 })
    .When("assert it with 'satisfies { it > 0 }'", { one -> one satisfies { it > 0 } })
    .Then("success", successCase(1, true))
    .When("assert it with 'satisfies { it < 0 }'", { one -> one satisfies { it < 0 } })
    .Then("failure", failCase(1, false))
