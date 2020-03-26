package org.mikeneck.check.assertion

import org.mikeneck.check.Assertion
import org.mikeneck.check.CheckContext
import org.mikeneck.check.CheckDescription
import org.mikeneck.check.Timer
import java.time.Instant

val checkDescription: CheckDescription = object : CheckDescription {
  override val id: String get() = "id"
  override val givenDescription: String get() = "description"
  override val whenDescription: String get() = "when"
  override val thenDescription: String get() = "then"
}

fun checkContext(given: Any, `when`: Any?, instant: Instant = Instant.now()): CheckContext = object : CheckContext {
  override val timer: Timer
    get() = Timer(instant)
  override val given: Any get() = given
  override val `when`: Any? get() = `when`
}

fun <C: Any, A: Any, G: Any, W> successCase(given: G, `when`: W): C.(A, Assertion) -> Assertion = { _, assertion ->
  if (assertion.result(checkDescription, checkContext(given, `when`)) == null) Assertion.success()
  else Assertion.fail()
}

fun <C: Any, A: Any, G: Any, W> failCase(given: G, `when`: W): C.(A, Assertion) -> Assertion = { _, assertion ->
  if (assertion.result(checkDescription, checkContext(given, `when`)) != null) Assertion.success()
  else Assertion.fail()
}
