pluginManagement {
    repositories {
        gradlePluginPortal()   // still check the portal first
        mavenCentral()         // then Maven Central
    }
}

// (also make sure your project modules are included and you have a repo for dependencies:)
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "tx-eventbridge"
include("common", "jdbc", "r2dbc")
