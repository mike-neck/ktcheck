package run.ktcheck

import java.time.Clock
import java.time.Instant

class PropertyGwtImpl<C : Any, G : Any, W>(
    private val ktPropertyDescription: KtPropertyDescription,
    private val beforeAction: () -> C,
    private val givenAction: C.() -> G,
    private val whenAction: C.(G) -> W,
    private val thenAssertion: C.(G, W) -> Assertion,
    private val afterAction: C.(G) -> Unit,
    private val clock: Clock = Clock.systemUTC()
) : KtProperty, KtPropertyDescription by ktPropertyDescription {

  private val unhandledException: (String, Throwable) -> Unsuccessful = { tag, throwable ->
    Unsuccessful.ByUnhandledException(listOf(ktPropertyDescription.id, tag, ktPropertyDescription.name), throwable)
  }

  private fun Throwable.toCheckResult(start: Instant): CheckResult = when(this) {
    is Unsuccessful.ByUnhandledException -> CheckResult.error(ktPropertyDescription, Timer(start, clock), this)
    is Unsuccessful.ByAbortion -> CheckResult.skip(ktPropertyDescription, Timer(start, clock), this)
    is Unsuccessful.BySkip -> CheckResult.skip(ktPropertyDescription, Timer(start, clock), this)
    else -> CheckResult.error(ktPropertyDescription, Timer(start, clock), unhandledException("unknown", this))
  }

  override fun perform(): CheckResult = perform(Instant.now(clock))

  private fun perform(start: Instant): CheckResult =
      (ThrowableOnLeft(Either.right(Unit), unhandledException))("before") {
        GivenContext(Timer(start, clock), beforeAction())
      }("given") {
        it.accept(givenAction)
      }("when") {
        it.accept(whenAction)
      }("then") {
        it.assertion(thenAssertion)
      }("after") {
        it.accept(afterAction)
      }("finishing") {
        it.result(ktPropertyDescription)
      }.rescue { it.toCheckResult(start) }
}

private data class GivenContext<C : Any>(
    val timer: Timer,
    val value: C
) {
  fun <G : Any> accept(givenAction: C.() -> G): WhenContext<C, G> =
      WhenContext(this, value.givenAction())
}

private data class WhenContext<C : Any, G : Any>(
    val givenContext: GivenContext<C>,
    val givenValue: G
) {
  fun <W> accept(whenAction: C.(G) -> W): ThenContext<C, G, W> =
      ThenContext(this, baseContext.whenAction(givenValue))

  internal val baseContext: C get() = givenContext.value
}

private data class ThenContext<C : Any, G : Any, W>(
    val whenContext: WhenContext<C, G>,
    val whenValue: W
) {
  fun assertion(thenAssertion: C.(G, W) -> Assertion): AfterContext<C, G, W> =
      AfterContext(this, baseContext.thenAssertion(whenContext.givenValue, whenValue))

  internal val given: G get() = whenContext.givenValue

  internal val baseContext: C get() = whenContext.baseContext
}

private data class AfterContext<C : Any, G : Any, W>(
    val thenContext: ThenContext<C, G, W>,
    val assertion: Assertion
) {

  private infix fun <T: Any> Unit.returns(t: T): T = this.let { t }

  fun accept(afterAction: C.(G) -> Unit): FinishContext = 
      baseContext.afterAction(given) returns FinishContext(
          object : KtPropertyContext {
            override val timer: Timer get() = thenContext.whenContext.givenContext.timer
            override val given: Any get() = thenContext.given
            override val `when`: Any? get() = thenContext.whenValue
          },
          assertion
      )

  internal val given: G get() = thenContext.given

  internal val baseContext: C get() = thenContext.baseContext
}

private data class FinishContext(
    val ktPropertyContext: KtPropertyContext,
    val assertion: Assertion
) {
  fun result(ktPropertyDescription: KtPropertyDescription): CheckResult = assertion.toCheckResult(ktPropertyDescription, ktPropertyContext)
}
