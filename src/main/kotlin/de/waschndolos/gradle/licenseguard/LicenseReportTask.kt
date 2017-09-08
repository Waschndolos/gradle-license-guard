package de.waschndolos.gradle.licenseguard

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.waschndolos.gradle.licenseguard.model.DependencyInformation
import de.waschndolos.gradle.licenseguard.model.LicenseReport
import de.waschndolos.gradle.licenseguard.parsing.ManifestParser
import de.waschndolos.gradle.licenseguard.parsing.PomParser
import de.waschndolos.gradle.licenseguard.report.PdfReportCreator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*

open class LicenseReportTask : DefaultTask() {

    @OutputFile
    val outputFile: File = File(project.buildDir.path + "/report/" + project.name + "_license-report.pdf")


    @InputFile
    private val logo: File = File(project.projectDir.path + "/logo.png")

    @Input
    private val reportDescription: String = "This reports lists all dependencies of " + project.name + ". " +
            "It shall give you an overview about your 3rd party licenses."

    init {
        group = "license"
        description = "Checks your external dependencies license information and generates a pdf report"
    }

    @TaskAction
    fun createReport() {

        println("Starting to create License report of configuration \"runtime\" for project " + project.name)

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

        println("3 - Creating now License report in " + outputFile.path)

        var logoBase64 = ""
        if (logo != null) {
          logoBase64 = Base64.getEncoder().encodeToString(logo.readBytes())
        }

        dependencyInformations.sortBy { (name) -> name }
        val licenseReport = LicenseReport(project.name, dependencyInformations, logoBase64, reportDescription)

        val xmlMapper = XmlMapper()
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT) // pretty print
        xmlMapper.enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)

        val xmlFile = File(project.buildDir.path + "/report/" + project.name + "_license-report-data.xml")
        xmlMapper.writeValue(xmlFile, licenseReport)

        println("3 - done...")


        println("4 - Creating now PDF report")

        val pdfReportCreator = PdfReportCreator()
        pdfReportCreator.createPdfFromXmlReport(xmlFile, outputFile.path)

        println("4 - done...")
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
