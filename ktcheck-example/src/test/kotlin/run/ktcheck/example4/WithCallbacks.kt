package run.ktcheck.example4

import run.ktcheck.Given
import run.ktcheck.KtCheck
import run.ktcheck.Unsuccessful
import run.ktcheck.assertion.NoDep.shouldBe

object WithCallbacks: KtCheck
by Given(
    description = "With callbacks", 
    before = {
      println("before")
      mutableListOf("foo", "bar", "baz", "qux") 
    }, 
    after = { _, assertion ->
      println(this)
      if (assertion.isSuccess) {
        println("success")
      }
    },
    finish = { u: Unsuccessful? ->
      if (u != null) {
        println("something strange")
      }
      println(u) 
    },
    action = { 
      println("given action")
      object: Function1<List<String>, List<String>> {
        override fun invoke(list: List<String>): List<String> {
          return list + "quux"
        }
      }
    })
    .When("add an item", { function ->
      println("when action")
      function(this)
    })
    .Then("the size is 6", { _, list ->
      println("then action")
      list.size shouldBe 6 
    })


object IntPlusTest: KtCheck
by Given(
    description = "Int の値 1 に",
    before = { Unit },
    after = { _, assertion -> println(assertion.isSuccess) },
    finish = { unsuccessful -> 
      if (unsuccessful != null) println(unsuccessful.message)
    },
    action = { 1 })
    .When("Int の値 3 を足すと", { int -> if (int < 2) throw IllegalStateException("Given value($int) is smaller than 2") else int + 3 })
    .Then("4 になる", {_, result -> result shouldBe 4})
