package de.waschndolos.gradle.licenseguard

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

class LicenseGuardTask : DefaultTask() {

    @OutputFile
    val outputFile: File = File(project.rootProject.buildDir.path + "/licenseGuard.xml")

    init {
        group = "license"
        description = "Checks your external dependencies license information and generates a report"
    }

    @TaskAction
    fun createReport() {
        outputFile.parentFile.mkdirs()
    }

}