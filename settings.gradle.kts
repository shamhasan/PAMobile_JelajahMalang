pluginManagement {
    repositories {
        google()  // ← Hapus content filter, biarkan akses penuh
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()  // ← Sudah benar
        mavenCentral()  // ← Sudah benar
    }
}

rootProject.name = "PAMobile-JelahjahMalang"
include(":app")