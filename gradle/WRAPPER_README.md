# Gradle Wrapper

This project uses the Gradle Wrapper. To generate the wrapper jar after cloning:

```bash
# Option A: If you have Gradle installed locally
gradle wrapper --gradle-version 8.8

# Option B: From Android Studio
# Open the project — Android Studio will offer to generate the wrapper automatically.
```

The `gradle/wrapper/gradle-wrapper.properties` file is included. The actual `gradle-wrapper.jar` is a binary file excluded from this ZIP to keep it small; run one of the commands above (or open in Android Studio) to materialise it.

After generating, build with:

```bash
./gradlew assembleDebug      # Linux/Mac
gradlew.bat assembleDebug    # Windows
```
