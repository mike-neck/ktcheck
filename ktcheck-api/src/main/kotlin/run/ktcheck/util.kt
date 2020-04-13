package run.ktcheck

import java.time.Clock
import java.time.Duration
import java.time.Instant

data class Timer(private val started: Instant, private val clock: Clock = Clock.systemUTC()) {

  fun stop(): Duration = Duration.between(started, Instant.now(clock))
}

private fun Throwable.throwOnOutOfMemoryError(): Throwable =
    when (this) {
      is OutOfMemoryError -> throw this
      else -> this
    }

internal class ThrowableOnLeft<C: Any, T: Any>(
    private val initialContext: C?,
    private val either: Either<Unsuccessful, T>,
    private val throwable: (String, Throwable) -> Unsuccessful) {

  fun <N: Any> map(mapping: (T) -> N): ThrowableOnLeft<C, N> = ThrowableOnLeft(initialContext, either.map(mapping), this.throwable)

  operator fun <N: Any> invoke(
      description: String,
      throwable: (String, Throwable) -> Unsuccessful = this.throwable,
      mapping: (T) -> N
  ): ThrowableOnLeft<C, N> =
      try {
        map(mapping)
      } catch (e: Throwable) {
        ThrowableOnLeft(
            initialContext,
            Either.left(throwable(description, e.throwOnOutOfMemoryError())), 
            throwable)
      }

  fun doFinally(action: C.(Unsuccessful?) -> Unit): Either<Unsuccessful, T> =
      when (initialContext) {
        null -> either
        else -> when (either) {
          is Left -> either.apply { initialContext.action(value) }
          else -> either.apply { initialContext.action(null) }
        }
      }

  fun rescue(mapping: (Throwable) -> T): T = either.rescue(mapping)

  companion object {
    fun <T: Any> initialContext(description: String, throwable: (String, Throwable) -> Unsuccessful, initial: () -> T): ThrowableOnLeft<T, T> =
        try {
          initial().let { ThrowableOnLeft(it, Either.right(it), throwable) }
        } catch (e: Throwable) {
          ThrowableOnLeft(
              null,
              Either.left(throwable(description, e.throwOnOutOfMemoryError())),
              throwable
          )
        }
  }
}
