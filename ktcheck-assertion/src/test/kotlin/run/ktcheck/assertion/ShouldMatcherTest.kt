package run.ktcheck.assertion

import run.ktcheck.Given
import run.ktcheck.KtCheck
import run.ktcheck.assertion.AnyMatchers.be
import run.ktcheck.assertion.IterableMatchers.containAll
import run.ktcheck.assertion.NoDep.should
import run.ktcheck.assertion.StringMatchers.contain

object ShouldBeTest: KtCheck
by Given("1", { 1 })
    .When("assert it with 'should be(1)'",{ one -> one should be(1) })
    .Then("success", successCase(1, 1))
    .When("assert it with 'should be(2)'", { one -> one should be(2) })
    .Then("failure", failCase(1, 2))

object ShouldContainAllTest: KtCheck
by Given("[foo,bar,qux]", { listOf("foo", "bar", "qux") })
    .When("assert it with should containAll(foo,bar)'", { list -> list should containAll("foo", "bar") })
    .Then("success", successCase(listOf("foo", "bar", "qux"), listOf("foo", "bar")))
    .When("assert it with 'should containAll(bar,baz)'", { list -> list should containAll("bar", "baz") })
    .Then("failure", failCase(listOf("foo", "bar", "qux"), listOf("bar", "baz")))

object ShouldContainStringTest: KtCheck
by Given("foo/bar/baz", { "foo/bar/baz" })
    .When("assert it with 'should contain(r/b)'", { string -> string should contain("r/b") })
    .Then("success", successCase("foo/bar/baz", "r/b"))
    .When("assert it with 'should contain(qux/)'", { string -> string should contain("qux/") })
    .Then("failure", failCase("foo/bar/baz", "qux/"))
