package org.mikeneck.check.engine

import org.junit.platform.engine.TestDescriptor

interface Execution: TestDescriptor {

  fun execute(listener: ExecutionListener)

  fun children(): Iterable<Execution>

  override fun getChildren(): MutableSet<out TestDescriptor> = children().toMutableSet()

  companion object {
    inline operator fun <T: Any> Unit.invoke(nextAction: () -> T): T = nextAction()
  }
}
