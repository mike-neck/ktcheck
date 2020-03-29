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
package run.ktcheck.example1

import run.ktcheck.Given
import run.ktcheck.KtCheck
import run.ktcheck.assertion.NoDep.shouldBe

object HelloWorld : KtCheck
by Given({ "Hello" })
    .When({ "World" })
    .Then({ hello, world -> "$hello, $world" shouldBe "Hello, World" })
