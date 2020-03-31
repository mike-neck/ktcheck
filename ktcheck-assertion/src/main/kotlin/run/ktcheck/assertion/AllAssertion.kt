package run.ktcheck.assertion

import run.ktcheck.Assertion

object AllAssertion {

  private fun wrap(assertion: () -> Assertion): () -> Assertion = {      
    try {
          assertion()
    } catch (e: Throwable) {
      when (e) {
        is OutOfMemoryError -> throw e
        else -> Assertion.fail()
      }
    }
  }

  fun all(vararg assertions: () -> Assertion): Assertion =
      CompositeAssertion(assertions.map { wrap(it) }.map { it() })
}
