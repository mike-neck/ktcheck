package run.ktcheck

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertAll
import kotlin.reflect.KClass

infix fun <T: Any> T.shouldBe(expected: T): Unit = assertEquals(expected, this)

infix fun <T: Any> Iterable<T>.allSatisfies(predicate: (T) -> Pair<String, Boolean>): Unit =
    assertAll(this.map { actual -> { 
      predicate(actual).let { assertTrue(it.second, it.first) } 
    } })

infix fun <T: Any> Iterable<T>.all(ast: Assert<T>): Unit =
    assertAll(this.map { actual -> { ast.run(actual) } })

interface Assert<T: Any> {
  fun run(t: T)

  companion object {
    operator fun <T: Any> invoke(ast: (T) -> Unit): Assert<T> =
        object : Assert<T> {
          override fun run(t: T): Unit = ast(t) 
        }
  }
}

infix fun Any?.shouldBeInstanceOf(klass: KClass<*>): Unit =
    assertTrue(klass.isInstance(this), "${this?.javaClass?.kotlin} should be $klass")
