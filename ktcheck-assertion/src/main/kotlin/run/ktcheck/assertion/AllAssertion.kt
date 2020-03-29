/*
 * Copyright 2020 Shinya Mochida
 * 
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
