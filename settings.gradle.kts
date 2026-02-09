pluginManagement {
    includeBuild("build-tools")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "Vonage Video Android"
include(":app")
include(":vonage-video-ui-compose")
include(":vonage-video-core")
include(":vonage-video-shared")
include(":vonage-feature-chat")
include(":vonage-feature-archiving")
include(":vonage-feature-screensharing")
include(":vonage-feature-reactions")
include(":vonage-feature-video-effects")
include(":vonage-feature-captions")
include(":vonage-audio-selector")
include(":vonage-config-idea-plugin")
