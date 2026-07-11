[![Maven Central](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Forg%2Fjraf%2Fklibminitel%2Fklibminitel%2Fmaven-metadata.xml&style=flat-square&label=maven-central&strategy=latestProperty)](https://central.sonatype.com/namespace/org.jraf.klibminitel) [![Maven Snapshots](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Forg%2Fjraf%2Fklibminitel%2Fklibminitel%2Fmaven-metadata.xml&style=flat-square&label=snapshots&color=%2315252D&strategy=latestProperty)](https://central.sonatype.com/repository/maven-snapshots/org/jraf/klibminitel)

# klibminitel

A Kotlin (Multiplatform) library to interact with the [Minitel](https://en.wikipedia.org/wiki/Minitel).

## Usage

```kotlin
implementation("org.jraf.klibminitel:klibminitel:1.4.0")
```

```kotlin
val minitel = Minitel(filePath)
minitel.connect {
  screen.disableAcknowledgement()
  screen.localEcho(false)
  screen.clearScreenAndHome()
  screen.print("Hello, World!")

  coroutineScope.launch {
    system.collect { e ->
      onSystemEvent(e, screen)
    }
  }

  keyboard.collect { e ->
    onKeyboardEvent(e, screen)
  }
}
```

Full KDoc is available [here](https://jraf.org/klibminitel/kdoc/).

## Author and License
```
Copyright (C) 2019-present Benoit 'BoD' Lubek (BoD@JRAF.org)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
