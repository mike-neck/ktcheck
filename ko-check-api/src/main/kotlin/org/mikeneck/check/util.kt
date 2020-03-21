package org.mikeneck.check

import java.time.Clock
import java.time.Duration
import java.time.Instant

data class Timer(private val started: Instant, private val clock: Clock = Clock.systemUTC()) {

  fun stop(): Duration = Duration.between(started, Instant.now(clock))
}
