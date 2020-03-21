import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.70"
}

val projectVersion: String by project

group = "org.mikeneck.ko-check"
version = projectVersion
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
  mavenCentral()
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
}
