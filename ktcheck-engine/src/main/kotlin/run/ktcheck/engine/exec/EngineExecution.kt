package run.ktcheck.engine.exec

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.TestTag
import org.junit.platform.engine.UniqueId
import run.ktcheck.KtCheck
import run.ktcheck.engine.Execution
import run.ktcheck.engine.Execution.Companion.invoke
import run.ktcheck.engine.ExecutionListener
import java.util.*

class EngineExecution(
    private val rootUniqueId: UniqueId,
    internal val allTests: Iterable<KtCheck>
): Execution {

  override fun execute(listener: ExecutionListener) =
      listener.onTestStart(this) () { children().forEach { it.execute(listener) } } () { listener.onTestSucceeded(this) }

  override fun children(): Iterable<Execution> = allTests.map { KtCheckExecution(this, it) }

  override fun getSource(): Optional<TestSource> = Optional.empty()

  override fun removeFromHierarchy() = Unit

  override fun setParent(parent: TestDescriptor?) = Unit

  override fun getParent(): Optional<TestDescriptor> = Optional.empty()

  override fun getDisplayName(): String = "ktcheck"

  override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER

  override fun getUniqueId(): UniqueId = rootUniqueId

  override fun removeChild(descriptor: TestDescriptor?) = Unit

  override fun addChild(descriptor: TestDescriptor?) = Unit

  override fun findByUniqueId(uniqueId: UniqueId?): Optional<out TestDescriptor> = 
      when (uniqueId) {
        this.uniqueId -> Optional.of(this)
        else -> children().map { it.findByUniqueId(uniqueId) }.find { it.isPresent } ?: Optional.empty()
      }

  override fun getTags(): MutableSet<TestTag> = mutableSetOf()
}