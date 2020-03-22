package org.mikeneck.check.engine

import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.UniqueId
import org.mikeneck.check.Either
import org.mikeneck.check.engine.exec.EngineListener

class KoCheckEngine(
    private val scanner: (EngineDiscoveryRequest, UniqueId) -> Execution
): TestEngine {

  @Suppress("unused")
  constructor(): this({ request, uniqueId -> ClasspathScanner(request, uniqueId).scanTests() })

  override fun discover(discoveryRequest: EngineDiscoveryRequest?, uniqueId: UniqueId?): TestDescriptor =
      discoveryRequest.either("invalid request") () { req ->
        uniqueId.either("invalid unique-id") ("create parames") { id -> req to id }
      } ("scan tests") {
        scanner(it.first, it.second) 
      }.throwOnLeft()

  override fun getId(): String = "ko-check"

  override fun execute(request: ExecutionRequest?) =
      if (request == null) throw IllegalArgumentException("request is null")  
      else when (val desc = request.rootTestDescriptor) {
        null -> throw IllegalArgumentException("descriptor is null")
        is Execution -> desc.execute(EngineListener(request.engineExecutionListener))
        else -> Unit
      }

  companion object {
    fun <T: Any> T?.either(message: String): Either<Throwable, T> =
        if (this == null) Either.left(IllegalStateException(message))
        else Either.right(this)
  }
}
