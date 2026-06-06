import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm")
  id("org.jetbrains.intellij.platform")
  id("org.jetbrains.intellij.platform.grammarkit")
}

repositories {
  mavenCentral()

  intellijPlatform {
    defaultRepositories()
  }
}

dependencies {
  intellijPlatform {
    intellijIdea("2026.1.1")
    bundledPlugin("com.intellij.java")

    pluginVerifier()
    zipSigner()
  }

  implementation(project(":core"))
  implementation(kotlin("stdlib"))

  testImplementation(kotlin("test"))
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(21))
  }

  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_21)
  }
}

val generatedLexerDir = layout.buildDirectory.dir("generated/sources/grammarKit/lexer")
val generatedParserDir = layout.buildDirectory.dir("generated/sources/grammarKit/parser")

sourceSets {
  main {
    java.srcDir(generatedLexerDir)
    java.srcDir(generatedParserDir)
  }
}

tasks {
  generateLexer {
    sourceFile.set(file("src/main/grammar/DeltaTPLexer.flex"))
    targetRootOutputDir.set(generatedLexerDir)
  }

  generateParser {
    sourceFile.set(file("src/main/grammar/DeltaTP.bnf"))
    targetRootOutputDir.set(generatedParserDir)
  }

  compileJava {
    dependsOn(generateLexer, generateParser)
  }

  compileKotlin {
    dependsOn(generateLexer, generateParser)
  }

  test {
    useJUnitPlatform()
  }

  patchPluginXml {
    sinceBuild.set("261")
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}