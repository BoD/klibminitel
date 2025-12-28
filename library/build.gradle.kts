import com.gradleup.librarian.gradle.Librarian

plugins {
  kotlin("multiplatform")
}

kotlin {
  jvm()
  macosArm64()
  linuxX64()
  linuxArm64()

  sourceSets {
    commonMain {
      dependencies {
        implementation(KotlinX.coroutines.core)
        implementation(KotlinX.datetime)
        api("org.jetbrains.kotlinx:kotlinx-io-core:_")
      }
    }

    jvmMain {
      dependencies {
        implementation(KotlinX.coroutines.jdk9)
      }
    }
  }
}

Librarian.module(project)
