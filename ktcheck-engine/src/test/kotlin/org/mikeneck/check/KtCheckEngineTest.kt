package org.mikeneck.check

import org.junit.platform.engine.ConfigurationParameters
import org.junit.platform.engine.DiscoveryFilter
import org.junit.platform.engine.DiscoverySelector
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.UniqueId
import org.mikeneck.check.engine.Execution
import org.mikeneck.check.engine.KtCheckEngine
import org.mikeneck.check.engine.exec.EngineExecution
import java.util.*

class KtCheckEngineTest {

  val tests: Iterable<KtCheck> = listOf(
      Given("1") { 1 }.When(" + 1") { it + 1 }.Then { _, _ -> Assertion.success() }
  )

  val engineDiscoveryRequest = object : EngineDiscoveryRequest {
    override fun getConfigurationParameters(): ConfigurationParameters = object : ConfigurationParameters {
      override fun getBoolean(key: String?): Optional<Boolean> = Optional.empty()
      override fun size(): Int = 0
      override fun get(key: String?): Optional<String> = Optional.empty()
    }

    override fun <T : DiscoveryFilter<*>?> getFiltersByType(filterType: Class<T>?): MutableList<T> = mutableListOf()
    override fun <T : DiscoverySelector?> getSelectorsByType(selectorType: Class<T>?): MutableList<T> = mutableListOf()
  }

  @org.junit.jupiter.api.Test
  fun scan(): Unit =
      Given<(EngineDiscoveryRequest, UniqueId) -> Execution>("scanner returns execution") {
        { _: EngineDiscoveryRequest, uniqueId: UniqueId -> EngineExecution(uniqueId, tests) }
      }.When("call discover") { KtCheckEngine(it).discover(engineDiscoveryRequest, UniqueId.forEngine("ktcheck")) }
          .Then("it is EngineExecution") { _, desc ->
            when (desc) {
              is EngineExecution -> if (desc.allTests == tests) Assertion.success() else Assertion.fail(tests, desc.allTests)
              else -> Assertion.fail()
            }
          }.runStandalone()
}
