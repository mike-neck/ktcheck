package org.mikeneck.check.assertion

import org.mikeneck.check.Given
import org.mikeneck.check.Test
import org.mikeneck.check.assertion.NoDep.should

object ShouldBeTest: Test
by Given("1", { 1 })
    .When("assert it with 'should be(1)'",{ one -> one should be(1) })
    .Then("success", successCase(1, 1))
    .When("assert it with 'should be(2)'", { one -> one should be(2) })
    .Then("failure", failCase(1, 2))
