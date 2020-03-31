package run.ktcheck.assertion

import run.ktcheck.Given
import run.ktcheck.KtCheck
import run.ktcheck.assertion.CompositeMatcher.Companion.and
import run.ktcheck.assertion.IterableMatchers.containAll
import run.ktcheck.assertion.IterableMatchers.haveSize
import run.ktcheck.assertion.NoDep.should

object CompositeMatcherTest : KtCheck
by Given(" [foo,bar,baz]", { listOf("foo", "bar", "baz") })
    .When("append qux", { it + "qux" })
    .Then("it contains all (bar, qux) and its size == 4", { _, result ->
      result should (containAll<String, List<String>>("bar", "qux") and haveSize(4))
    })
