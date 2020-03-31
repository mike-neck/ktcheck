package run.ktcheck.example1

import run.ktcheck.Given
import run.ktcheck.KtCheck
import run.ktcheck.assertion.NoDep.shouldBe

object HelloWorld : KtCheck
by Given({ "Hello" })
    .When({ "World" })
    .Then({ hello, world -> "$hello, $world" shouldBe "Hello, World" })
