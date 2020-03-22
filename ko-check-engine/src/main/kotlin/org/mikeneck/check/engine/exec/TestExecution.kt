package org.mikeneck.check.engine.exec

import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestSource
import org.junit.platform.engine.TestTag
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.ClassSource
import org.mikeneck.check.Test
import org.mikeneck.check.engine.Execution
import org.mikeneck.check.engine.ExecutionListener
import java.util.*

class TestExecution(
    private val parentExecution: Execution,
    private val test: Test
): Execution {

  override fun execute(listener: ExecutionListener) = children().forEach { it.execute(listener) }

  override fun children(): Iterable<Execution> = test.all.map { CheckExecution(this, test, it) }

  override fun getSource(): Optional<TestSource> = Optional.of(ClassSource.from(test.javaClass.canonicalName))

  override fun removeFromHierarchy() = Unit

  override fun setParent(parent: TestDescriptor?) = Unit

  override fun getParent(): Optional<TestDescriptor> = Optional.of(parentExecution)

  override fun getDisplayName(): String = test.javaClass.simpleName

  override fun getType(): TestDescriptor.Type = TestDescriptor.Type.CONTAINER

  override fun getUniqueId(): UniqueId = parentExecution.uniqueId.append("test", displayName)

  override fun removeChild(descriptor: TestDescriptor?) = Unit

  override fun addChild(descriptor: TestDescriptor?) = Unit

  override fun findByUniqueId(uniqueId: UniqueId?): Optional<out TestDescriptor> =
      when (uniqueId) {
        this.uniqueId -> Optional.of(this)
        else -> children().find { it.findByUniqueId(uniqueId).isPresent }?.let { Optional.of(it) } ?: Optional.empty()
      }

  override fun getTags(): MutableSet<TestTag> = mutableSetOf()
}
