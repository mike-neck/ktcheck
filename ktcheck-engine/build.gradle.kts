import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files

plugins {
  kotlin("jvm") version "1.3.70"
  id("org.jetbrains.dokka") version "0.10.1"
}

if (rootProject.hasProperty("sonatypeUrl") || rootProject.hasProperty("githubPackageUrl")) {
  project.apply("from" to rootProject.file("release.gradle.kts"))
}

group = rootProject.group
version = rootProject.version

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
  mavenCentral()
  jcenter()
}

dependencies {
  val kotlinVersion: String by project
  implementation(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))
  implementation(project(":ktcheck-api"))
  val junitEngineVersion: String by project
  val openTest4jVersion: String by project
  implementation("org.junit.platform:junit-platform-engine:$junitEngineVersion")
  implementation("org.opentest4j:opentest4j:$openTest4jVersion")
  implementation("io.github.classgraph:classgraph:4.8.65")

  val junitVersion: String by project
  testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "1.8"
  }
  if (name == "compileKotlin") {
    finalizedBy("serviceLoaderDescription")
  }
}

task("serviceLoaderDescription") {
  val kotlinCompile = tasks.getByPath("compileKotlin") as KotlinCompile
  dependsOn(kotlinCompile)

  val dir = kotlinCompile.destinationDir
  val path = dir.toPath().resolve("META-INF/services/org.junit.platform.engine.TestEngine")
  outputs.file(path)

  doLast { 
    if (!Files.exists(path.parent)) {
      Files.createDirectories(path.parent)
    }
    Files.write(path, "run.ktcheck.engine.KtCheckEngine".toByteArray())
  }
}

val TaskContainer.dokka: TaskProvider<org.jetbrains.dokka.gradle.DokkaTask>
  get() = named<org.jetbrains.dokka.gradle.DokkaTask>("dokka")

tasks.dokka {
  subProjects = listOf("ktcheck-api")
}
