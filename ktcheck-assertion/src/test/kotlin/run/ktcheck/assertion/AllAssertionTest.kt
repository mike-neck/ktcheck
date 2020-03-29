package run.ktcheck.assertion

import run.ktcheck.Given
import run.ktcheck.KtCheck
import run.ktcheck.assertion.AllAssertion.all
import run.ktcheck.assertion.IterableMatchers.containAll
import run.ktcheck.assertion.IterableMatchers.haveSize
import run.ktcheck.assertion.NoDep.should

object AllAssertionTest: KtCheck
by Given("list[foo,bar,baz]", { listOf("foo", "bar", "baz") })
    .When("append qux", { it + "qux" })
    .Then("assert it with size(4) and contain(foo,qux)", { _, list ->
      all(
          { list should haveSize(4) },
          { list should containAll("foo", "qux") }
      ) })
    .When("assert it with all(size(2), containAll(foo, bar))", { list ->
      all(
          { list should haveSize(4) },
          { list should containAll("foo", "qux") }
      ) })
    .Then("failure", failCase("list(foo,bar,baz)", "all(size(4), containAll(foo, bar))"))
