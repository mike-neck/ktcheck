package org.mikeneck.check

import java.time.Duration

interface CheckResult {
  val name: String
  val executionTime: Duration
  val unsuccessful: Unsuccessful?
  val success: Boolean get() = unsuccessful != null
}
