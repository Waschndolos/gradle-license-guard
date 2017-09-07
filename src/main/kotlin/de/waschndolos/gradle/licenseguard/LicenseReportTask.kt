package de.waschndolos.gradle.licenseguard

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import de.waschndolos.gradle.licenseguard.model.DependencyInformation
import de.waschndolos.gradle.licenseguard.model.LicenseReport
import de.waschndolos.gradle.licenseguard.parsing.ManifestParser
import de.waschndolos.gradle.licenseguard.parsing.PomParser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class LicenseReportTask : DefaultTask() {

    @OutputFile
    private val outputFile: File = File(project.rootProject.buildDir.path + "/report/license-report.xml")

    init {
        group = "license"
        description = "Checks your external dependencies license information and generates a report"
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
        val licenseReport = LicenseReport(project.name, dependencyInformations)

        val xmlMapper = XmlMapper()
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT) // pretty print

        xmlMapper.writeValue(outputFile, licenseReport)

        println("3 - done...")
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
