package de.waschndolos.gradle.licenseguard

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.waschndolos.gradle.licenseguard.conversion.DependencyInformationsToHintConverter
import de.waschndolos.gradle.licenseguard.conversion.MapToDependencyInformationConverter
import de.waschndolos.gradle.licenseguard.model.DependencyInformation
import de.waschndolos.gradle.licenseguard.model.LicenseReport
import de.waschndolos.gradle.licenseguard.parsing.ManifestParser
import de.waschndolos.gradle.licenseguard.parsing.PomParser
import de.waschndolos.gradle.licenseguard.report.PdfReportCreator
import de.waschndolos.gradle.licenseguard.report.RtfReportCreator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*

open class LicenseReportTask : DefaultTask() {

    @OutputFile
    val outputFile: File = File(project.buildDir.path + "/report/" + project.name + "_license-report")

    @Input
    var licenseMap: Map<String, String> = mutableMapOf()

    @Input
    var logo: String? = null

    @Input
    var projectName: String? = project.name

    @Input
    var excludedDependencies: List<String> = mutableListOf()

    @TaskAction
    fun createReport() {

        println("Starting to create License report of configuration \"runtime\" for project " + projectName)

        project.configurations.create("poms")

        project.configurations.getByName("runtime").resolvedConfiguration.lenientConfiguration.artifacts.forEach { resolvedArtifact ->
            val id = resolvedArtifact.moduleVersion.id

            var name = resolvedArtifact.name
            if (name == null) {
                name = id.name
            }
            project.dependencies.add("poms", id.group + ":" + name + ":" + id.version + "@pom")
        }

        outputFile.parentFile.mkdirs()

        println("1 - Starting to collect license information from pom.")
        val licensesFromPOM = collectLicensesFromPOM()
        println("1 - done...")

        val dependencyInformations = mutableListOf<DependencyInformation>()

        licensesFromPOM.filterValues { it.isNotEmpty() }.forEach { key, value ->
            dependencyInformations.add(DependencyInformation(key, value, "pom"))
        }

        val missingLicenses = licensesFromPOM.filterValues { it.isEmpty() }.keys

        println("2 - Starting to collect license information from Manifest.")
        val licensesFromManifest = collectLicensesFromManifest(missingLicenses)
        licensesFromManifest.forEach { key, value ->
            dependencyInformations.add(DependencyInformation(key, value, "manifest"))
        }
        println("2 - done...")

        println("3 - Adding manually determined Licenses.")
        excludedDependencies.forEach { excludedDIName ->
            val foundDIs = mutableListOf<DependencyInformation>()

            dependencyInformations.forEach { di ->
                if (Regex(excludedDIName).matches(di.name)) {
                    foundDIs.add(di)
                }
            }
            foundDIs.forEach { foundDI ->
                dependencyInformations.remove(foundDI)
            }
        }

        MapToDependencyInformationConverter().convert(licenseMap).forEach { manualDI ->
            val foundDIs = mutableListOf<DependencyInformation>()
            dependencyInformations.forEach { di ->
                if (di.name.startsWith(manualDI.name)) {
                    foundDIs.add(di)
                }
            }
            foundDIs.forEach { foundDI ->
                dependencyInformations.remove(foundDI)
                dependencyInformations.add(manualDI)
            }

        }
        println("3 - done...")

        println("4 - Creating now License report in " + outputFile.path)

        var logoBase64 = ""
        if (logo != null && "" != logo) {
          logoBase64 = Base64.getEncoder().encodeToString(File(logo).readBytes())
        }

        dependencyInformations.sortBy { (name) -> name }
        val licenseReport = LicenseReport(projectName, dependencyInformations, logoBase64, "This reports lists all dependencies of " + projectName + ". " +
                "It shall give you an overview about your 3rd party licenses.")

        val xmlMapper = XmlMapper()
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT) // pretty print
        xmlMapper.enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)

        val xmlFile = File(project.buildDir.path + "/report/" + project.name + "_license-report-data.xml")
        xmlMapper.writeValue(xmlFile, licenseReport)

        println("4 - done...")

        println("5 - Creating now PDF report")

        val pdfReportCreator = PdfReportCreator()
        pdfReportCreator.createReport(xmlFile, outputFile.path)

        println("5 - done...")
        println("Report created: " + outputFile.path)

        println("6 - Creating now RTF report")
        val rtfReportCreator = RtfReportCreator()
        rtfReportCreator.createReport(xmlFile, outputFile.path)
        println("6 - done...")

        val noLicensesFound = dependencyInformations.filter { dep -> dep.license.isEmpty() }

        if (noLicensesFound.isNotEmpty()) {
            println("------------------------")
            println("Add this to your task configuration and fill out missing licenses manually:")
            println(DependencyInformationsToHintConverter().convert(noLicensesFound))
            println("------------------------")
        }

    }

    private fun collectLicensesFromManifest(missingLicenses: Set<String>): MutableMap<String, List<String>> {
        project.logger.info("Checking Licenses from Manifest...")

        val licenses : MutableMap<String, List<String> > = mutableMapOf()

        val manifestParser = ManifestParser()
        project.configurations.getByName("runtime").forEach{ file ->

            if (missingLicenses.contains(file.name.removeSuffix(".jar"))) {
                val license = manifestParser.parseManifest(file)
                licenses.put(file.name, license)
            }
        }

        return licenses
    }


    private fun collectLicensesFromPOM() : MutableMap<String, List<String> > {
        project.logger.info("Checking Licenses from pom...")

        val licenses : MutableMap<String, List<String> > = mutableMapOf()

        val parser = PomParser()
        project.configurations.getByName("poms").forEach { pom ->
            val pomFile = pom.absoluteFile
            val parseLicense = parser.parseLicense(pomFile)

            project.logger.debug("pom={} - license={}", pom, licenses)
            licenses.put(pom.name.removeSuffix(".pom"), parseLicense)
        }

        return licenses
    }
}
