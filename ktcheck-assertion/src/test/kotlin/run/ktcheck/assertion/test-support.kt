package run.ktcheck.assertion

import run.ktcheck.Assertion
import run.ktcheck.KtPropertyContext
import run.ktcheck.KtPropertyDescription
import run.ktcheck.Timer
import java.time.Instant

val ktPropertyDescription: KtPropertyDescription = object : KtPropertyDescription {
  override val id: String get() = "id"
  override val givenDescription: String get() = "description"
  override val whenDescription: String get() = "when"
  override val thenDescription: String get() = "then"
}

fun checkContext(given: Any, `when`: Any?, instant: Instant = Instant.now()): KtPropertyContext = object : KtPropertyContext {
  override val timer: Timer
    get() = Timer(instant)
  override val given: Any get() = given
  override val `when`: Any? get() = `when`
}

fun <C: Any, A: Any, G: Any, W> successCase(given: G, `when`: W): C.(A, Assertion) -> Assertion = { _, assertion ->
  if (assertion.result(ktPropertyDescription, checkContext(given, `when`)) == null) Assertion.success()
  else Assertion.fail()
}

fun <C: Any, A: Any, G: Any, W> failCase(given: G, `when`: W): C.(A, Assertion) -> Assertion = { _, assertion ->
  if (assertion.result(ktPropertyDescription, checkContext(given, `when`)) != null) Assertion.success()
  else Assertion.fail()
}
