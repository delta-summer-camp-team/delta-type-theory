pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

rootProject.name = "delta-theorem-prover-intellij-plugin"

include(":core")
include(":intellij-plugin")