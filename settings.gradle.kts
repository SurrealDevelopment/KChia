
rootProject.name = "KChia"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(":ChiaWebPlayground", ":ChiaBlockchainKt")