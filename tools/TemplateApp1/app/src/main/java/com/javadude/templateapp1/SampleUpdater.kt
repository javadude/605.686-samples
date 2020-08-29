package com.javadude.templateapp1

import java.io.File

//val gitIgnore = File(".gitignore")
val gradleProperties = File("gradle.properties")
//val rootBuildGradle = File("build.gradle")
val gradle = File("gradle")
val gradlewBat = File("gradlew.bat")
val gradlew = File("gradlew")

fun main(args: Array<String>) {
    val currentDir = File(System.getProperty("user.dir")!!)
    val dir = currentDir.parentFile!!.parentFile!!
    val projects = dir.findProjects().filterNot { it == currentDir }
    projects.forEach { it.update() }
}
private fun File.findProjects() : List<File> =
    when {
        File(this, "app").exists() -> listOf(this)
        this.isDirectory -> this.listFiles().flatMap { it.findProjects() }
        else -> emptyList()
    }

fun File.update() {
    print("Updating $this...")
    deleteIfExists("gradle")
    deleteIfExists(".gradle")
    deleteIfExists(".idea")
    deleteIfExists("captures")
    this.deleteIml()
    this.deleteBuildDirs()

    gradle.copyRecursively(File(this, "gradle"), overwrite = true)
    gradlew.copyTo(File(this, "gradlew"), overwrite = true)
    gradlewBat.copyTo(File(this, "gradlew.bat"), overwrite = true)
//    rootBuildGradle.copyTo(File(this, "build.gradle"), overwrite = true)
    gradleProperties.copyTo(File(this, "gradle.properties"), overwrite = true)
//    gitIgnore.copyTo(File(this, ".gitignore"), overwrite = true)
    println(" (done)")
}

private fun File.deleteIfExists(name : String) {
    val file = File(this, name)
    if (file.exists()) {
        file.deleteRecursively()
    }
}
private fun File.deleteIml() {
    if (this.name.endsWith(".iml")) {
        this.delete()
        return
    }
    if (this.isDirectory) {
        this.listFiles()!!.forEach { it.deleteIml() }
    }
}
private fun File.deleteBuildDirs() {
    if (this.name == "build" && this.isDirectory) {
        this.deleteRecursively()
        return
    }
    if (this.isDirectory) {
        this.listFiles()!!.forEach { it.deleteBuildDirs() }
    }
}