package org.mikeneck.check.engine.exec

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.TestTag
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.ClassSource
import org.mikeneck.check.KtCheck
import org.mikeneck.check.engine.Execution
import org.mikeneck.check.engine.Execution.Companion.invoke
import org.mikeneck.check.engine.ExecutionListener
import java.util.*

class KtCheckExecution(
    private val parentExecution: Execution,
    private val ktCheck: KtCheck
): Execution {

  override fun execute(listener: ExecutionListener) =
      listener.onTestStart(this) () { children().forEach { it.execute(listener) } } () { listener.onTestSucceeded(this) }

  override fun children(): Iterable<Execution> = ktCheck.all.map { CheckExecution(this, ktCheck, it) }

  override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(ktCheck.javaClass.canonicalName))

  override fun removeFromHierarchy() = Unit

  override fun setParent(parent: TestDescriptor?) = Unit

  override fun getParent(): Optional<TestDescriptor> = Optional.of(parentExecution)

  override fun getDisplayName(): String = ktCheck.javaClass.simpleName

  override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER

  override fun getUniqueId(): UniqueId = parentExecution.uniqueId.append("test", displayName)

  override fun removeChild(descriptor: TestDescriptor?) = Unit

  override fun addChild(descriptor: TestDescriptor?) = Unit

  override fun findByUniqueId(uniqueId: UniqueId?): Optional<out TestDescriptor> =
      when (uniqueId) {
        this.uniqueId -> Optional.of(this)
        else -> children().map { it.findByUniqueId(uniqueId) }.find { it.isPresent } ?: Optional.empty()
      }

  override fun getTags(): MutableSet<TestTag> = mutableSetOf()
}
