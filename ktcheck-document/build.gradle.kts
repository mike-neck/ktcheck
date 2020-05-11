import org.asciidoctor.gradle.jvm.AsciidoctorTask

plugins {
  id("org.asciidoctor.jvm.convert") version "3.1.0"
}

repositories {
  mavenCentral()
}

asciidoctorj {
  modules {
    diagram.use()
    diagram.version("1.5.16")
  }
}

tasks {
  "asciidoctor"(AsciidoctorTask::class) {
    baseDirFollowsSourceFile()
  }
}
