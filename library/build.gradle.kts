plugins {
  kotlin("multiplatform")
  id("maven-publish")
  id("org.jetbrains.dokka")
  id("signing")
}

// Generate a Version.kt file with a constant for the version name
val generateVersionKtTask = tasks.register("generateVersionKt") {
  val outputDir = layout.buildDirectory.dir("generated/source/kotlin").get().asFile
  outputs.dir(outputDir)
  doFirst {
    val outputWithPackageDir = File(outputDir, "org/jraf/klibminitel/internal").apply { mkdirs() }
    File(outputWithPackageDir, "Version.kt").writeText(
      """
        package org.jraf.klibminitel.internal

        internal const val VERSION = "${project.version}"
      """.trimIndent()
    )
  }
}

// Generate Javadoc (Dokka) Jar
tasks.register<Jar>("dokkaHtmlJar") {
  archiveClassifier.set("javadoc")
  from("${layout.buildDirectory}/dokka")
  dependsOn(tasks.dokkaGenerate)
}

kotlin {
  jvm()
  jvmToolchain(11)
  macosArm64()
  linuxX64()
  linuxArm64()

  sourceSets {
    commonMain {
      kotlin.srcDir(generateVersionKtTask)
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

publishing {
  repositories {
    maven {
      // Note: declare your user name / password in your home's gradle.properties like this:
      // mavenCentralNexusUsername = <user name>
      // mavenCentralNexusPassword = <password>
      url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
      name = "mavenCentralNexus"
      credentials(PasswordCredentials::class)
    }
  }

  publications.withType<MavenPublication>().forEach { publication ->
    publication.artifact(tasks.getByName("dokkaHtmlJar"))

    publication.pom {
      name.set("klibminitel")
      description.set("A Kotlin (JVM) library to interact with the Minitel")
      url.set("https://github.com/BoD/klibminitel")
      licenses {
        license {
          name.set("The Apache License, Version 2.0")
          url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
          distribution.set("repo")
        }
      }
      developers {
        developer {
          id.set("BoD")
          name.set("Benoit 'BoD' Lubek")
          email.set("BoD@JRAF.org")
          url.set("https://JRAF.org")
          organization.set("JRAF.org")
          organizationUrl.set("https://JRAF.org")
          roles.set(listOf("developer"))
          timezone.set("+1")
        }
      }
      scm {
        connection.set("scm:git:https://github.com/BoD/klibminitel")
        developerConnection.set("scm:git:https://github.com/BoD/klibminitel")
        url.set("https://github.com/BoD/klibminitel")
      }
      issueManagement {
        url.set("https://github.com/BoD/klibminitel/issues")
        system.set("GitHub Issues")
      }
    }
  }
}

signing {
  // Note: declare the signature key, password and file in your home's gradle.properties like this:
  // signing.keyId=<8 character key>
  // signing.password=<your password>
  // signing.secretKeyRingFile=<absolute path to the gpg private key>
  sign(publishing.publications)
}

// Workaround for https://youtrack.jetbrains.com/issue/KT-46466
val dependsOnTasks = mutableListOf<String>()
tasks.withType<AbstractPublishToMaven>().configureEach {
  dependsOnTasks.add(this.name.replace("publish", "sign").replaceAfter("Publication", ""))
  dependsOn(dependsOnTasks)
}

dokka {
  dokkaPublications.html {
    outputDirectory.set(rootProject.file("docs"))
  }
}

// Run `./gradlew dokkaHtml` to generate the docs
// Run `./gradlew publishToMavenLocal` to publish to the local maven repo
// Run `./gradlew publish` to publish to Maven Central (then go to https://oss.sonatype.org/#stagingRepositories and "close", and "release")
