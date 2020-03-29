package run.ktcheck.engine.exec

import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestExecutionResult
import run.ktcheck.Unsuccessful
import run.ktcheck.engine.Execution
import run.ktcheck.engine.ExecutionListener

class EngineListener (
    private val listener: EngineExecutionListener
): ExecutionListener {

  override fun onTestStart(execution: Execution) = listener.executionStarted(execution)

  override fun onTestSucceeded(execution: Execution) = listener.executionFinished(execution, TestExecutionResult.successful())

  override fun onTestAssertionFailed(execution: Execution, failure: Unsuccessful.ByAssertionFailure) =
      listener.executionFinished(execution, TestExecutionResult.failed(failure))

  override fun onTestSkipped(execution: Execution, skip: Unsuccessful.BySkip) =
      listener.executionSkipped(execution, skip.toString())

  override fun onTestAborted(execution: Execution, abortion: Unsuccessful.ByAbortion) =
      listener.executionFinished(execution, TestExecutionResult.aborted(abortion))

  override fun onTestUnexpectedError(execution: Execution, error: Unsuccessful.ByUnhandledException) =
      listener.executionFinished(execution, TestExecutionResult.failed(error))
}
