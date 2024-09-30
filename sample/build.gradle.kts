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

  // Slf4j
  implementation("org.slf4j:slf4j-simple:_")

  // Library
  implementation(project(":klibminitel"))

  // OpenAI
  implementation("org.jraf:klibopenai:_")
}
