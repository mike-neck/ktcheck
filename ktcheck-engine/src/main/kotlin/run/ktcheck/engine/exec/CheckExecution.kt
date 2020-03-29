package run.ktcheck.engine.exec

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.TestTag
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.MethodSource
import run.ktcheck.KtProperty
import run.ktcheck.KtCheck
import run.ktcheck.Unsuccessful
import run.ktcheck.engine.Execution
import run.ktcheck.engine.Execution.Companion.invoke
import run.ktcheck.engine.ExecutionListener
import java.util.*

class CheckExecution(
    private val parentExecution: Execution,
    private val container: KtCheck,
    private val ktProperty: KtProperty
): Execution {

  override fun execute(listener: ExecutionListener) = 
      listener.onTestStart(this) () {
        ktProperty.perform()
      }.let { result -> 
        when {
          result.success -> listener.onTestSucceeded(this)
          else -> when (val unsuccessful = result.unsuccessful) {
            null -> listener.onTestUnexpectedError(
                this, 
                Unsuccessful.ByUnhandledException(
                    listOf(ktProperty.id, ktProperty.name),
                    IllegalStateException("test is not successful but exception not found, ${result.name}")))
            is Unsuccessful.ByAbortion -> listener.onTestAborted(this, unsuccessful)
            is Unsuccessful.BySkip -> listener.onTestSkipped(this, unsuccessful)
            is Unsuccessful.ByAssertionFailure -> listener.onTestAssertionFailed(this, unsuccessful)
            is Unsuccessful.ByUnhandledException -> listener.onTestUnexpectedError(this, unsuccessful)
            else -> listener.onTestUnexpectedError(
                this, 
                Unsuccessful.ByUnhandledException(
                    listOf(ktProperty.id, ktProperty.name), IllegalStateException("unknown state", unsuccessful)))
          }
        }
      }

  override fun children(): Iterable<Execution> = emptySet()

  override fun getSource(): Optional<TestSource> = Optional.of(MethodSource.from(container.javaClass.canonicalName, ktProperty.name))

  override fun removeFromHierarchy() = Unit

  override fun setParent(parent: TestDescriptor?) = Unit

  override fun getParent(): Optional<TestDescriptor> = Optional.of(parentExecution)

  override fun getDisplayName(): String = ktProperty.name

  override fun getType(): TestDescriptor.Type = TestDescriptor.Type.TEST

  override fun getUniqueId(): UniqueId = parentExecution.uniqueId.append("check", ktProperty.id)

  override fun removeChild(descriptor: TestDescriptor?) = Unit

  override fun addChild(descriptor: TestDescriptor?) = Unit

  override fun findByUniqueId(uniqueId: UniqueId?): Optional<out TestDescriptor> = 
      if (this.uniqueId == uniqueId) Optional.of(this)
      else Optional.empty()

  override fun getTags(): MutableSet<TestTag> = mutableSetOf()
}
