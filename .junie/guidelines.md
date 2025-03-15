# klibminitel Developer Guidelines

## Project Overview
- **Type**: Kotlin Multiplatform Library (KMP)
- **Purpose**: Interact with Minitel devices
- **Platforms**: JVM, macOS ARM64, Linux (x64, ARM64)

## Project Structure
```
klibminitel/
├── library/               # Main library module
│   ├── src/
│   │   ├── commonMain/    # Shared Kotlin code
│   │   └── jvmMain/      # JVM-specific code
├── sample/                # Sample applications
└── docs/                  # Generated documentation
```

## Development Setup
1. **Requirements**
   - JDK 11 or higher
   - Gradle (wrapper included)

2. **Build & Test**
   ```bash
   ./gradlew build        # Build the project
   ./gradlew test         # Run tests
   ```

## Common Tasks
- Generate documentation: `./gradlew dokkaHtml`
- Publish to local Maven: `./gradlew publishToMavenLocal`
- Update dependencies: `./gradlew refreshVersions`

## Best Practices
1. **Code Organization**
   - Place shared code in `commonMain`
   - Platform-specific code goes in respective source sets

2. **Testing**
   - Write tests for new features
   - Ensure existing tests pass before committing

3. **Documentation**
   - Document public APIs with KDoc
   - Keep README.md updated with latest changes

4. **Version Control**
   - Follow semantic versioning
   - Update CHANGELOG.md for significant changes

## Publishing
1. Local testing: `./gradlew publishToMavenLocal`
2. Maven Central release:
   - Configure credentials in ~/.gradle/gradle.properties
   - Run `./gradlew publish`
   - Visit https://oss.sonatype.org/#stagingRepositories
   - "Close" and "Release" the repository
