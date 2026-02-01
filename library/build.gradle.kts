import com.gradleup.librarian.gradle.Librarian

plugins {
  alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
  jvm()
  macosArm64()
  linuxX64()
  linuxArm64()

  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.datetime)
        api(libs.kotlinx.io.core)
      }
    }

    jvmMain {
      dependencies {
        implementation(libs.kotlinx.coroutines.jdk9)
      }
    }
  }
}

Librarian.module(project)
