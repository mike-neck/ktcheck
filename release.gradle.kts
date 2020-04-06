import org.gradle.api.tasks.bundling.Jar

plugins.apply("maven-publish")

fun Project.publishing(configure: PublishingExtension.() -> Unit): Unit =
    (this as ExtensionAware).extensions.configure("publishing", configure)
val Project.publishing: PublishingExtension get() =
  (this as ExtensionAware).extensions.getByName("publishing") as PublishingExtension
val Project.sourceSets: SourceSetContainer get() =
  (this as ExtensionAware).extensions.getByName("sourceSets") as SourceSetContainer
val SourceSetContainer.main: NamedDomainObjectProvider<SourceSet>
  get() = named<SourceSet>("main")

val sourcesJar by tasks.registering(Jar::class) {
  archiveClassifier.set("sources")
  from(sourceSets.main.get().allSource)
}

val dokkaTask by tasks.named("dokka")
val dokkaJar by tasks.registering(Jar::class) {
  archiveClassifier.set("javadoc")
  from(dokkaTask)
}

publishing {
  repositories {
    if (project.hasProperty("sonatypeUrl")) {
      maven {
        name = "sonatype"
        val sonatypeUrl: String by project
        url = uri(sonatypeUrl)
        val sonatypeUsername: String? by project
        val sonatypePassword: String? by project
        credentials {
          username = sonatypeUsername
          password = sonatypePassword
        }
      }
    }
    if (project.hasProperty("githubPackageRepositoryUrl")) {
      maven { 
        name = "githubPackages"
        val githubPackageUrl: String by project
        url = uri(githubPackageUrl)
        val githubUsername: String? by project
        val githubToken: String? by project
        credentials { 
          username = githubUsername
          password = githubToken
        }
      }
    }
  }
  publications {
    create<MavenPublication>("library") {
      from(components["kotlin"])
      artifact(sourcesJar.get())
      artifact(dokkaJar.get())

      pom { 
        description.set("Testing framework on JUnit Platform with Give-When-Then style.")
        url.set("https://github.com/mike-neck/ktcheck")

        licenses { 
          license { 
            name.set("MIT License")
            url.set("https://opensource.org/licenses/mit-license.php")
          }
        }
        developers { 
          developer { 
            id.set("mike-neck")
            name.set("Shinya Mochida")
            email.set("jkrt3333[at]gmail.com")
          }
        }
        scm { 
          connection.set("scm:git:https://github.com/mike-neck/ktcheck.git")
          developerConnection.set("scm:git:https://github.com/mike-neck/ktcheck.git")
          url.set("https://github.com/mike-neck/ktcheck")
        }
      }
    }
  }
}

val privateKey: String? by project
val pgpPassword: String? by project
if (privateKey != null && pgpPassword != null) {
  plugins.apply("signing")

  fun Project.signing(configure: SigningExtension.() -> Unit): Unit =
      (this as ExtensionAware).extensions.configure("signing", configure)

  signing {
    useInMemoryPgpKeys(privateKey, pgpPassword)
    sign(publishing.publications["sonatype"])
  }
}

task("showType") {
  doLast {
    val publishToMavenLocal = tasks.getByPath("publish")
    println(publishToMavenLocal.javaClass.canonicalName)
    println(publishToMavenLocal.group)
    println(publishToMavenLocal.dependsOn.map { it.toString() })
  }
}

if (!project.hasProperty("KT_CHECK_VERSION")) {
  tasks.find { it.name == "publishLibraryPublicationToSonatypeRepository" }?.enabled = false
}
