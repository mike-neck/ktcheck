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

import run.ktcheck.Given
import run.ktcheck.KtCheck
import run.ktcheck.assertion.AllMatcher.Companion.and
import run.ktcheck.assertion.IterableMatchers.containAll
import run.ktcheck.assertion.IterableMatchers.haveSize
import run.ktcheck.assertion.NoDep.should

object AllMatcherTest : KtCheck
by Given(" [foo,bar,baz]", { listOf("foo", "bar", "baz") })
    .When("append qux", { it + "qux" })
    .Then("it contains all (bar, qux) and its size == 4", { _, result ->
      result should (containAll<String, List<String>>("bar", "qux") and haveSize(4))
    })
