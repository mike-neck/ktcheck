package org.mikeneck.check

import java.time.Duration

sealed class CheckResult(
    checkDescription: CheckDescription,
    checkContext: CheckContext,
    @Suppress("MemberVisibilityCanBePrivate")
    val unsuccessful: Unsuccessful?,
    private val result: String
) {

  @Suppress("MemberVisibilityCanBePrivate")
  val name: String = checkDescription.name

  val executionTime: Duration = checkContext.timer.stop()

  val success: Boolean get() = unsuccessful != null

  override fun toString(): String = "CheckResult[$name=$result]"

  companion object {

    fun success(checkDescription: CheckDescription, checkContext: CheckContext): CheckResult =
        Success(checkDescription, checkContext)

    fun failure(
        checkDescription: CheckDescription, checkContext: CheckContext, unsuccessful: Unsuccessful): CheckResult =
        Failure(checkDescription, checkContext, unsuccessful)
  }

  class Success(
      checkDescription: CheckDescription, checkContext: CheckContext
  ) : CheckResult(checkDescription, checkContext, null, "SUCCESS")

  class Failure(
      checkDescription: CheckDescription, checkContext: CheckContext, unsuccessful: Unsuccessful
  ) : CheckResult(checkDescription, checkContext, unsuccessful, "SUCCESS")
}
