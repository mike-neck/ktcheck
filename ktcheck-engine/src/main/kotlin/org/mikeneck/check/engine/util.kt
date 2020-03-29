package org.mikeneck.check.engine

import org.mikeneck.check.Either

operator fun <T: Any, R: Any> Either<Throwable, T>.invoke(actionName: String, action: (T) -> R): Either<Throwable, R> =
    try {
      this.map(action)
    } catch (e: Throwable) {
      when(e) {
        is OutOfMemoryError -> throw e
        else -> Either.left(IllegalStateException("exception when $actionName", e))
      }
    }

operator fun <T: Any, R: Any> Either<Throwable, T>.invoke(
    action: (T) -> Either<Throwable, R>): Either<Throwable, R> = this.flatMap(action)

fun <T: Any> Either<Throwable, T>.throwOnLeft(): T = this.rescue { throw it }
