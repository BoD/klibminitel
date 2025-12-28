plugins {
  kotlin("jvm")
  application
}

kotlin {
  jvmToolchain(11)
}

application {
  mainClass.set("org.jraf.klibminitel.sample.MainKt")
}

dependencies {
  // Kotlin
  implementation(KotlinX.coroutines.jdk9)

  // Logging
  implementation("org.jraf.klibnanolog:klibnanolog:_")

  // Library
  implementation(project(":klibminitel"))

  // OpenAI
  implementation("org.jraf.klibopenai:klibopenai:_")
}

// See https://github.com/BoD/k2o/pull/4
configurations.named { it == "mainSourceElements" }.configureEach {
  attributes {
    attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, "sources"))
  }
}
