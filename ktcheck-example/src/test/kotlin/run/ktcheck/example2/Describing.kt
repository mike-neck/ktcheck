package run.ktcheck.example2

import run.ktcheck.Given
import run.ktcheck.KtCheck
import run.ktcheck.assertion.NoDep.shouldBe

object Describing : KtCheck
by Given("A word 'Hello'", { "Hello" })
    .When("combined to a word 'World' with separator <,>", { "$it, World" })
    .Then("it should be 'Hello, World'", { _, sentence -> sentence shouldBe "Hello, World" })
