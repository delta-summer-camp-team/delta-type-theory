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
            kotlin.exclude("test/**")
        }
        test {
            kotlin.srcDir("test")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
