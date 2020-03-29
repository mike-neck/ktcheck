package org.mikeneck.check.engine

import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassNameFilter
import org.junit.platform.engine.discovery.ClassSelector
import org.mikeneck.check.Either
import org.mikeneck.check.KtCheck
import org.mikeneck.check.engine.exec.EngineExecution
import java.io.InputStreamReader
import java.util.function.Predicate
import kotlin.reflect.KClass

/**
 * Scans entire classpath, and retrieves [KtCheck] objects.
 */
class ClasspathScanner(
    private val request: EngineDiscoveryRequest,
    private val uniqueId: UniqueId
) {

  private fun koCheckApiExcludes(): Array<String> =
      Thread.currentThread().contextClassLoader.getResourceAsStream("ktcheck-api-excludes.txt")?.use { 
        InputStreamReader(it).readLines().toTypedArray()
      }?: emptyArray()

  private fun includeClasses(): Iterable<Predicate<String>> =
      request.getFiltersByType(ClassNameFilter::class.java).map { it.toPredicate() }

  private val requestClasses: List<String> = request.getSelectorsByType(ClassSelector::class.java).map { it.className }

  fun scanTests(): EngineExecution = try {
    ClassGraph()
        .enableClassInfo()
        .blacklistClasses(*koCheckApiExcludes())
        .scan()
        .use { scanResult -> 
          Either.right<Throwable, ScanResult>(scanResult) ("retrieve information on Test implementation classes") {
            it.getClassesImplementing(KtCheck::class.qualifiedName)
          } ("load classes") { classInfoList ->  
            classInfoList.filter {
              requestClasses.contains(it.name)
            }.map {
              @Suppress("UNCHECKED_CAST")
              it.loadClass().kotlin as KClass<KtCheck>
            }
          } ("get object instance") { classList ->
            classList.mapNotNull { it.objectInstance }
          } ("create EngineExecution") { allTests ->
            EngineExecution(uniqueId, allTests)
          }
        }.throwOnLeft()
  } catch (e: Throwable) {
    when (e) {
      is OutOfMemoryError -> throw e
      is IllegalStateException -> throw e
      else -> throw IllegalStateException("scan error", e)
    }
  }

  companion object {
    fun List<String>.tailPermutation(sep: String): List<String> =
        mutableListOf<String>().also {
          this.mapIndexed { index, _ -> 
            this.subList(index, this.size).joinToString(sep)
          }
        }.toList()
  }
}
