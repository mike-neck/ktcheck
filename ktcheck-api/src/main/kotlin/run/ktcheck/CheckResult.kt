package run.ktcheck

import java.time.Duration

sealed class CheckResult(
    ktPropertyDescription: KtPropertyDescription,
    val executionTime: Duration,
    @Suppress("MemberVisibilityCanBePrivate")
    val unsuccessful: Unsuccessful?,
    private val result: String
) {

  constructor(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext, unsuccessful: Unsuccessful?, result: String):
      this(ktPropertyDescription, ktPropertyContext.executionTime(), unsuccessful, result)

  constructor(ktPropertyDescription: KtPropertyDescription, timer: Timer, unsuccessful: Unsuccessful, result: String):
      this(ktPropertyDescription, timer.stop(), unsuccessful, result)

  @Suppress("MemberVisibilityCanBePrivate")
  val name: String = ktPropertyDescription.name

  val success: Boolean get() = unsuccessful == null

  override fun toString(): String = "CheckResult[($name)=$result]"

  companion object {

    fun success(ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext): CheckResult =
        CheckResult.Success(ktPropertyDescription, ktPropertyContext)

    fun failure(
        ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext, unsuccessful: Unsuccessful): CheckResult =
        CheckResult.Failure(ktPropertyDescription, ktPropertyContext, unsuccessful)

    fun error(
        ktPropertyDescription: KtPropertyDescription, timer: Timer, unsuccessful: Unsuccessful): CheckResult =
        CheckResult.Error(ktPropertyDescription, timer, unsuccessful)

    fun skip(
        ktPropertyDescription: KtPropertyDescription, timer: Timer, unsuccessful: Unsuccessful): CheckResult =
        CheckResult.Skip(ktPropertyDescription, timer, unsuccessful)
  }

  class Success(
      ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext
  ) : CheckResult(ktPropertyDescription, ktPropertyContext, null, "SUCCESS")

  class Failure(
      ktPropertyDescription: KtPropertyDescription, ktPropertyContext: KtPropertyContext, unsuccessful: Unsuccessful
  ) : CheckResult(ktPropertyDescription, ktPropertyContext, unsuccessful, "FAILURE")

  class Error(
      ktPropertyDescription: KtPropertyDescription, timer: Timer, unsuccessful: Unsuccessful
  ) : CheckResult(ktPropertyDescription, timer, unsuccessful, "ERROR")

  class Skip(
      ktPropertyDescription: KtPropertyDescription, timer: Timer, unsuccessful: Unsuccessful
  ) : CheckResult(ktPropertyDescription, timer, unsuccessful, "SKIPPED")
}
