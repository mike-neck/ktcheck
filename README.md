# ktcheck
ktcheck is a test framework for Kotlin working on JUnit platform, with Given-When-Then style.

---

Install
---

Use maven or Gradle.

### Maven

`pom.xml`

```xml
<project>
  <dependencies>
    <dependency>
      <group>run.ktcheck</group>
      <artifactId>ktcheck</artifactId>
      <version>v0.1.0</version>
    </dependency>
  </dependencies>
  <build>
      <plugins>
          <plugin>
              <artifactId>maven-surefire-plugin</artifactId>
              <version>2.22.2</version>
          </plugin>
          <plugin>
              <artifactId>maven-failsafe-plugin</artifactId>
              <version>2.22.2</version>
          </plugin>
      </plugins>
  </build>
</project>
```

### Gradle

`build.gradle`

```groovy
dependencies {
  testImplementation 'run.ktcheck:ktcheck:v0.1.0'
}

test {
  useJUnitPlatform()
}
```

`build.gradle.kts`

```kotlin
dependencies {
  testImplementation("run.ktcheck:ktcheck:v0.1.0")
}

test {
  useJUnitPlatform()
}
```

Usage
---

### Via JUnit Platform

#### 1. Import ktcheck API

After creating new Kotlin file, add ktcheck APIs.(of course you can import via IDE's auto completion.)

```kotlin
import run.ktcheck.KtCheck
import run.ktcheck.Given
```

#### 2. Create kotlin object, which implements `KtCheck`

```kotlin
object YourTest: KtCheck
```

#### 3. Implement `KtCheck` with `Given` class using kotlin delegation property `by`.

- In a `Given` phrase, write description and function which returns a condition object.
- In a `When` phrase, write description and function which takes the condition object and returns an action result.
- In a `Then` phrase, write description and function which takes the condition object and the action result, and returns an assertion result.

```kotlin
object YourTest: KtCheck
by Given("TimeService with fixed clock", { TimeService(fixedClock) })
    .When("get current time from TimeService#now", { timeService -> timeService.now() })
    .Then("it should be fixed time", { _, instant -> instant shouldBe fixedInstant })
```

To create `Assertion` object, use these functions.

- `run.ktcheck.assertion.NoDep.shouldBe`
- `run.ktcheck.assertion.NoDep.shouldNotBe`
- `run.ktcheck.assertion.NoDep.should` and `run.ktcheck.assertion.Matcher`

If the condition object is not needed, a simple asserting function is available.

- `run.ktcheck.assertion.NoDep.expect`
- `run.ktcheck.assertion.NoDep.expectNull`
- `run.ktcheck.assertion.NoDep.expectNotNull`

```kotlin
object YourTest: KtCheck
by Given("TimeService with fixed clock", { TimeService(fixedClock) })
    .When("get current time from TimeService#now", { timeService -> timeService.now() })
    .Then("it should be fixed time", expect(fixedInstant))
```

#### 4. Run test via JUnit Platform

```shell session
$ ./mvnw test
```

```shell session
$ ./gradlew test
```

### From main program

#### 1. Import ktcheck API

After creating new Kotlin file, add ktcheck APIs.(of course you can import via IDE's auto completion.)

```kotlin
import run.ktcheck.Given
```

#### 2. Create and run `KtCheck` object in main program from `Given` class.

- Create `KtCheck` object in main program from `Given` class.
- Run `KtCheck` using `runStandalone()` function.
- If test fails, it will throws `Unsuccessful` exception.

```kotlin
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun main() {
  val fixedInstant =  OffsetDateTime.of(2020, 1, 2, 15, 4, 5, 6, ZoneOffset.UTC).toInstant()
  val fixedClock = Clock.fixed(fixedInstant, ZoneOffset.UTC)
  val check = Given("TimeService with fixed clock") { TimeService(fixedClock) }
      .When("get current time from TimeService#now") { timeService -> timeService.now() }
      .Then("it should be fixed time") { _, instant -> instant shouldBe fixedInstant }
  check.runStandalone()
}
```

project dependencies
---

![dependencies](http://www.plantuml.com/plantuml/png/U05rZZ4Emo0CHNTEmGNOEdNeHL075Baf91W4pltxJOUaQZ8blETlVxzrQgwsChUGdpPqgy0OlnGtlMbQ1bkc7RXGuu3u7cb7JcBXh-joSCHpP1e2lyQdZQEukWPvbPQFEX4RNognK9TR6aoVncWjwzm3NUpTnbnNoXWBE0aB28MhSDfBtmfg_0KuTP96RX__jw1gjSU9rfLNsU4W4_65lDxx0VEMYMS0)
