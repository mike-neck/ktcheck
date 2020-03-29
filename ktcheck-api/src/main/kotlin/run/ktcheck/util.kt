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

internal class ThrowableOnLeft<T: Any>(
    private val either: Either<Throwable, T>,
    private val throwable: (String, Throwable) -> Throwable) {

  fun <N: Any> map(mapping: (T) -> N): ThrowableOnLeft<N> = ThrowableOnLeft(either.map(mapping), this.throwable)

  operator fun <N: Any> invoke(
      description: String,
      throwable: (String, Throwable) -> Throwable = this.throwable,
      mapping: (T) -> N
  ): ThrowableOnLeft<N> =
      try {
        map(mapping)
      } catch (e: Throwable) {
        ThrowableOnLeft(
            Either.left(throwable(description, e.throwOnOutOfMemoryError())), 
            throwable)
      }

  fun rescue(mapping: (Throwable) -> T): T = either.rescue(mapping)
}
