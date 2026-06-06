pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

rootProject.name = "delta-type-theory-intellij-plugin"

include(":core")
include(":intellij-plugin")