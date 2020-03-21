package org.mikeneck.check

import java.time.Duration

sealed class CheckResult(
    checkDescription: CheckDescription,
    val executionTime: Duration,
    @Suppress("MemberVisibilityCanBePrivate")
    val unsuccessful: Unsuccessful?,
    private val result: String
) {

  constructor(checkDescription: CheckDescription, checkContext: CheckContext, unsuccessful: Unsuccessful?, result: String):
      this(checkDescription, checkContext.executionTime(), unsuccessful, result)

  constructor(checkDescription: CheckDescription, timer: Timer, unsuccessful: Unsuccessful, result: String):
      this(checkDescription, timer.stop(), unsuccessful, result)

  @Suppress("MemberVisibilityCanBePrivate")
  val name: String = checkDescription.name

  val success: Boolean get() = unsuccessful == null

  override fun toString(): String = "CheckResult[($name)=$result]"

  companion object {

    fun success(checkDescription: CheckDescription, checkContext: CheckContext): CheckResult =
        Success(checkDescription, checkContext)

    fun failure(
        checkDescription: CheckDescription, checkContext: CheckContext, unsuccessful: Unsuccessful): CheckResult =
        Failure(checkDescription, checkContext, unsuccessful)

    fun error(
        checkDescription: CheckDescription, timer: Timer, unsuccessful: Unsuccessful): CheckResult =
        Error(checkDescription, timer, unsuccessful)

    fun skip(
        checkDescription: CheckDescription, timer: Timer, unsuccessful: Unsuccessful): CheckResult =
        Skip(checkDescription, timer, unsuccessful)
  }

  class Success(
      checkDescription: CheckDescription, checkContext: CheckContext
  ) : CheckResult(checkDescription, checkContext, null, "SUCCESS")

  class Failure(
      checkDescription: CheckDescription, checkContext: CheckContext, unsuccessful: Unsuccessful
  ) : CheckResult(checkDescription, checkContext, unsuccessful, "FAILURE")

  class Error(
      checkDescription: CheckDescription, timer: Timer, unsuccessful: Unsuccessful
  ) : CheckResult(checkDescription, timer, unsuccessful, "ERROR")

  class Skip(
      checkDescription: CheckDescription, timer: Timer, unsuccessful: Unsuccessful
  ) : CheckResult(checkDescription, timer, unsuccessful, "SKIPPED")
}
