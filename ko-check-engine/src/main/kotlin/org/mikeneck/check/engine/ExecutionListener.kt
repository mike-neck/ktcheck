package org.mikeneck.check.engine

import org.mikeneck.check.Unsuccessful

interface ExecutionListener {

  fun onTestStart(execution: Execution)

  fun onTestSucceeded(execution: Execution)

  fun onTestAssertionFailed(execution: Execution, failure: Unsuccessful.ByAssertionFailure)

  fun onTestSkipped(execution: Execution, skip: Unsuccessful.BySkip)

  fun onTestAborted(execution: Execution, abortion: Unsuccessful.ByAbortion)

  fun onTestUnexpectedError(execution: Execution, error: Unsuccessful.ByUnhandledException)
}
