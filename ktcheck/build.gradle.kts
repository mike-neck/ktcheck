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
  api(project(":ktcheck-api"))
  api(project(":ktcheck-assertion"))

  implementation(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))
  implementation(project(":ktcheck-engine"))
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "1.8"
  }
}
