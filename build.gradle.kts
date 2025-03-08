allprojects {
  repositories {
    mavenLocal()
    mavenCentral()
  }

  group = "org.jraf"
  version = "1.3.1"
}

plugins {
  kotlin("multiplatform").apply(false)
}

// `./gradlew refreshVersions` to update dependencies
