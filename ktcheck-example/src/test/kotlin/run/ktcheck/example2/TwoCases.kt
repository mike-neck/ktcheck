package run.ktcheck.example2

import run.ktcheck.Given
import run.ktcheck.KtCheck
import run.ktcheck.assertion.NoDep.shouldBe

object TwoCases : KtCheck
by Given({ "Hello" })
    .When({ "world" })
    .Then({ hello, world -> "$hello, $world" shouldBe "Hello, world" })
    .When({ "KtCheck" })
    .Then({ hello, ktcheck -> "$hello, $ktcheck" shouldBe "Hello, KtCheck" })
