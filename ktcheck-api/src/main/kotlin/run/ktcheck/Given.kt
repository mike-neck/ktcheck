package run.ktcheck

class Given<C: Any, G: Any>(
    private val description: String,
    private val before: () -> C,
    private val after: C.(G) -> Unit = {},
    private val action: C.() -> G
): KtCheck {

  companion object {
    operator fun <G: Any> invoke(description: String, action: () -> G): Given<Unit, G> =
        Given(description, { Unit }, { Unit }, { action() })
  }

  private val list: MutableList<KtProperty> = mutableListOf()

  override val all: Iterable<KtProperty> get() = list.toMutableSet()

  private val name: String get() = 
    when (val n = this.javaClass.simpleName) {
      null -> "Given@${this.hashCode().toString(16)}"
      "Given" -> "$n@${this.hashCode().toString(16)}"
      else -> n
    }

  private val identities: IntArray = intArrayOf(0)

  private fun identity(): Int = ++identities[0]

  inner class When<W> (
      private val description: String,
      private val action: C.(G) -> W
  ) {

    @Suppress("FunctionName")
    fun Then(description: String = "", action: C.(G, W) -> Assertion): Given<C, G> =
        this@Given
            .apply { this@Given.list.add(PropertyGwtImpl(
                object : KtPropertyDescription {
                  override val id: String = "${this@Given.name}-${identity()}"
                  override val givenDescription: String get() = this@Given.description
                  override val whenDescription: String get() = this@When.description
                  override val thenDescription: String get() = description
                },
                before,
                this@Given.action,
                this@When.action,
                action,
                this@Given.after))
            }
  }
}
