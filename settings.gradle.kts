pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "IzmirEshot"

include(":app")
include(":core:common")
include(":core:model")
include(":core:network")
include(":core:database")
include(":core:data")
include(":feature:home")
include(":feature:line_detail")
include(":feature:stop_detail")
include(":feature:nearby")
include(":feature:favorites")
include(":feature:announcements")
include(":feature:map")
