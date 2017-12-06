package de.waschndolos.gradle.licenseguard.command.report

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.waschndolos.gradle.licenseguard.model.DependencyInformation
import de.waschndolos.gradle.licenseguard.model.LicenseReport
import org.gradle.api.Project
import java.io.File
import java.util.*

class CreateXMLCommand {

    fun execute(project: Project, logo: String, dependencyInformations: List<DependencyInformation>, projectName: String) : File {

        var logoBase64 = ""
        if ("" != logo) {
            logoBase64 = Base64.getEncoder().encodeToString(File(logo).readBytes())
        }

        dependencyInformations.sortedBy { it.name }
        val licenseReport = LicenseReport(projectName, dependencyInformations, logoBase64, "This reports lists all dependencies of " + projectName + ". " +
                "It shall give you an overview about your 3rd party licenses.")

        val xmlMapper = XmlMapper()
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT) // pretty print
        xmlMapper.enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)

        val xmlFile = File(project.buildDir.path + "/report/" + project.name + "_license-report-data.xml")
        xmlMapper.writeValue(xmlFile, licenseReport)
        return xmlFile
    }
}