package org.mikeneck.check

import java.time.Duration

/**
 * Represents each test execution.
 */
interface KtProperty: KtPropertyEntity, KtPropertyDescription

/**
 * Represents performance of each test execution.
 */
interface KtPropertyEntity {

  fun perform(): CheckResult
}

/**
 * Description of a test's condition, action, result. Also provides its id and name.
 */
interface KtPropertyDescription {

  val id: String

  val name: String get() = "Given $givenDescription, When $whenDescription, Then $thenDescription"

  val givenDescription: String

  val whenDescription: String

  val thenDescription: String
}

/**
 * Represents a context of each test execution.
 */
interface KtPropertyContext {

  val timer: Timer

  fun executionTime(): Duration = timer.stop()

  val given: Any

  val `when`: Any?
}
