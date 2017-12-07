package de.waschndolos.gradle.licenseguard

import de.waschndolos.gradle.licenseguard.command.*
import de.waschndolos.gradle.licenseguard.command.license.CollectFromManifest
import de.waschndolos.gradle.licenseguard.command.license.CollectFromPluginConfigurationCommand
import de.waschndolos.gradle.licenseguard.command.license.CollectFromPomCommand
import de.waschndolos.gradle.licenseguard.command.report.CreateReportCommand
import de.waschndolos.gradle.licenseguard.command.report.CreateXMLCommand
import de.waschndolos.gradle.licenseguard.model.DependencyInformation
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class LicenseReportTask : DefaultTask() {

    @OutputFile
    val outputFile: File = File(project.buildDir.path + "/report/" + project.name + "_license-report")

    @Input
    var licenseMap: Map<String, String> = mutableMapOf()

    @Input
    var logo: String = ""

    @Input
    var projectName: String = project.name

    @Input
    var excludedDependencies: List<String> = mutableListOf()

    @Input
    var outputFormat: List<String> = mutableListOf("pdf")

    @Input
    var configuration: String = "runtime"

    @TaskAction
    fun createReport() {

        outputFile.parentFile.mkdirs()

        project.logger.lifecycle("Starting to create License report of configuration \"{}\" for project {}", configuration, projectName)
        CreatePomConfigurationCommand().execute(project, configuration)

        val dependencyInformations = mutableListOf<DependencyInformation>()
        project.logger.lifecycle("Collecting license information from pom.")
        CollectFromPomCommand().execute(project, dependencyInformations)

        project.logger.lifecycle("Determine missing licenses.")
        val missingLicenses = DetermineEmptyLicenseCommand().execute(dependencyInformations)

        project.logger.lifecycle("Collecting license information from manifest.")
        CollectFromManifest(project, configuration).execute(missingLicenses, dependencyInformations)

        project.logger.lifecycle("Collecting licenses from plugin configuration.")
        CollectFromPluginConfigurationCommand(excludedDependencies, licenseMap).execute(dependencyInformations)

        project.logger.lifecycle("Creating now license report.")
        val xmlFile = CreateXMLCommand().execute(project, logo, dependencyInformations, projectName)

        project.logger.lifecycle("Creating now reports for the specified output formats: {}", outputFormat)
        CreateReportCommand().execute(outputFormat, xmlFile, outputFile.path)

        project.logger.debug("Checking if user needs to be informed.")
        NotifyUserCommand().execute(project, dependencyInformations)

        project.logger.debug("Deleting now xml file.")
        xmlFile.delete()

    }

}
