import java.time.Instant
import java.time.format.DateTimeFormatter

val libraryVersion: String =
    if (project.hasProperty("KT_CHECK_VERSION")) "${project.property("KT_CHECK_VERSION")}"
    else "${DateTimeFormatter.ISO_INSTANT.format(Instant.now()).replace(":", "")}-SNAPSHOT"

group = "run.ktcheck"
version = libraryVersion

tasks.create("buildAll") {
  group = "build"
  val subProjectsAssemble = subprojects
      .filter { it.name != "ktcheck-example" }
      .map { "${it.name}:assemble" }
  dependsOn(subProjectsAssemble.toTypedArray())
}

tasks.create("publishAll") {
  group = "publish"
  val subProjectsPublish = subprojects
      .filter { it.name != "ktcheck-example" }
      .map { "${it.name}:publish" }
  dependsOn(subProjectsPublish.toTypedArray())
}

tasks.create("publishLocalAll") {
  group = "publish"
  val subProjectsPublish = subprojects
      .filter { it.name != "ktcheck-example" }
      .map { "${it.name}:publishToMavenLocal" }
  dependsOn(subProjectsPublish.toTypedArray())
}
