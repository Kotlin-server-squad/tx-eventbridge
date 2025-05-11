plugins {
    id("java")
    kotlin("jvm") version "1.9.25" apply false
    kotlin("plugin.spring") version "1.9.25" apply false
    id("org.springframework.boot") version "3.4.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "com.tomaszezula"
    version = "0.0.1-SNAPSHOT"
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
    repositories {
        mavenCentral()
    }
    tasks.withType<Test> {
        useJUnitPlatform()
    }
}



