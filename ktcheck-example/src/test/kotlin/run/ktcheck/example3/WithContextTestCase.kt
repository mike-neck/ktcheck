package run.ktcheck.example3

import run.ktcheck.Given
import run.ktcheck.KtCheck
import run.ktcheck.assertion.AnyMatchers.be
import run.ktcheck.assertion.NoDep.should

class WithContextTestCase(
    private val originalList: List<String>
) {

  fun reversed(): List<String> = originalList.reversed()

  companion object: KtCheck
  by Given(
      description = "with list[foo,bar,baz], reversed list",
      before =  { WithContextTestCase(listOf("foo", "bar", "baz")) },
      action = { this.reversed() })
      .When("and reverse it", { it.reversed() })
      .Then("it becomes original one", { _, reversedTwice ->
        reversedTwice should be(this.originalList) 
      })
}
