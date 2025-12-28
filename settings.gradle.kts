rootProject.name = "klibminitel-root"

pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
  }
}

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
  }
}

plugins {
  // See https://jmfayard.github.io/refreshVersions
  id("de.fayard.refreshVersions") version "0.60.6"
}

include(":library")
project(":library").name = "klibminitel"

include(":sample")
