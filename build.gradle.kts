plugins {
  id("org.jetbrains.kotlin.jvm") version "2.3.0" apply false
  id("org.jetbrains.intellij.platform") version "2.16.0" apply false
  id("org.jetbrains.intellij.platform.grammarkit") version "2.16.0" apply false
}

allprojects {
  group = "delta"
  version = "1.0-SNAPSHOT"

  if (tasks.findByName("prepareKotlinBuildScriptModel") == null) {
    tasks.register("prepareKotlinBuildScriptModel") {
      group = "ide"
      description = "Compatibility task for IntelliJ Kotlin Gradle script import."
    }
  }
}

tasks.register("runIde") {
  dependsOn(":intellij-plugin:runIde")
}

tasks.register("buildPlugin") {
  dependsOn(":intellij-plugin:buildPlugin")
}

//tasks.register("generateLexer") {
//  dependsOn(":intellij-plugin:generateLexer")
//}
//
//tasks.register("generateParser") {
//  dependsOn(":intellij-plugin:generateParser")
//}