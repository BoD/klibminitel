import com.gradleup.librarian.gradle.Librarian

plugins {
  kotlin("multiplatform").apply(false)
  id("com.gradleup.librarian").apply(false)
}

Librarian.root(project)

// `./gradlew refreshVersions` to update dependencies
// `./gradlew publishToMavenLocal` to publish to local Maven repository
// `LIBRARIAN_SONATYPE_USERNAME=xxxx LIBRARIAN_SONATYPE_PASSWORD=xxxx LIBRARIAN_SIGNING_PRIVATE_KEY=`cat /path/to/armored/ascii/private-key.gpg` LIBRARIAN_SIGNING_PRIVATE_KEY_PASSWORD=xxxx ./gradlew librarianPublishToMavenCentral` to publish to Maven Central
