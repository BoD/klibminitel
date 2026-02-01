plugins {
  alias(libs.plugins.kotlin.jvm)
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
  implementation(libs.kotlinx.coroutines.jdk9)

  // Logging
  implementation(libs.klibnanolog)

  // Library
  implementation(project(":klibminitel"))

  // OpenAI
  implementation(libs.klibopenai)
}

// See https://github.com/BoD/k2o/pull/4
configurations.named { it == "mainSourceElements" }.configureEach {
  attributes {
    attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, "sources"))
  }
}
