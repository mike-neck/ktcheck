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

fun <T: Any> Either<Throwable, T>.throwOnLeft(): T = this.rescue { throw it }
