import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra["kotlin_version"] = "1.5.10"
    extra["kotlin_coroutines_core_version"] = "1.4.3"
    repositories {
        jcenter()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${project.extra["kotlin_version"]}")
    }
}
allprojects {
    group = "com.surrealdev.kchia"
    version = System.getenv("GITHUB_REF")?.split('/')?.last() ?: "development"
    repositories {
        jcenter()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        repositories {
            maven {
                name = "komputing/KHash GitHub Packages"
                url = uri("https://maven.pkg.github.com/komputing/KHash")
                credentials {
                    username = "token"
                    password = "\u0039\u0032\u0037\u0034\u0031\u0064\u0038\u0033\u0064\u0036\u0039\u0061\u0063\u0061\u0066\u0031\u0062\u0034\u0061\u0030\u0034\u0035\u0033\u0061\u0063\u0032\u0036\u0038\u0036\u0062\u0036\u0032\u0035\u0065\u0034\u0061\u0065\u0034\u0032\u0062"
                }
            }
        }

    }
}
