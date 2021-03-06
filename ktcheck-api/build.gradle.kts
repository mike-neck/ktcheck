import java.nio.file.Files
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
  api(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))
  api("org.jetbrains.kotlin:kotlin-reflect")
  api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))
  val openTest4jVersion: String by project
  implementation("org.opentest4j:opentest4j:$openTest4jVersion")

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
    finalizedBy("listExcludeClasses")
  }
}

task("listExcludeClasses") {
  val kotlinCompile = tasks.getByPath("compileKotlin") as KotlinCompile
  dependsOn(kotlinCompile)

  val path = kotlinCompile.destinationDir.toPath()
  val out = path.resolve("ktcheck-api-excludes.txt")

  outputs.file(out)

  doLast {
    val files = project.fileTree(kotlinCompile.destinationDir).files
        .asSequence()
        .filter { !it.name.contains("kotlin_module") }
        .filter { !it.name.matches(Regex(".+\\$[0-9]+\\.class$")) }
        .map { path.relativize(it.toPath()).toString() }
        .map { it.replace(".class", "") }
        .map { it.replace("/", ".") }
    Files.write(out, files.toMutableList(), Charsets.UTF_8)
  }
}
