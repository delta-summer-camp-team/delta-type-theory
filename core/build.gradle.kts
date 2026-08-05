plugins {
  id("org.jetbrains.kotlin.jvm")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib"))
  testImplementation(kotlin("test"))
}

kotlin {
  jvmToolchain(21)
  sourceSets {
    main {
      kotlin.srcDir(".")
      // "." would otherwise sweep the test sources into main
      kotlin.exclude("test/**", "build/**")
    }
    test {
      kotlin.srcDir("test")
    }
  }
}

tasks.test {
  useJUnitPlatform()
}
