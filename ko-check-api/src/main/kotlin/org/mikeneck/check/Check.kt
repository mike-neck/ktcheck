package org.mikeneck.check

/**
 * Represents each test execution.
 */
interface Check: CheckExecution, CheckDescription

/**
 * Represents performance of each test execution.
 */
interface CheckExecution {

  fun perform(): CheckResult
}

/**
 * Description of a test's condition, action, result. Also provides its id and name.
 */
interface CheckDescription {

  val id: String

  val name: String get() = "Given $givenDescription, When $whenDescription, Then $thenDescription"

  val givenDescription: String

  val whenDescription: String

  val thenDescription: String
}

/**
 * Represents a context of each test execution.
 */
interface CheckContext {

  val timer: Timer

  val given: Any

  val `when`: Any?
}
