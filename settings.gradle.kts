
rootProject.name = "kchia"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":chia-web-playground", ":kchia-blockchain")