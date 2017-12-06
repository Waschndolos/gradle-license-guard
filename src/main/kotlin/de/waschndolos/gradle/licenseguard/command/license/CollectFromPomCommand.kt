package de.waschndolos.gradle.licenseguard.command.license

import de.waschndolos.gradle.licenseguard.model.DependencyInformation
import de.waschndolos.gradle.licenseguard.parsing.PomParser
import org.gradle.api.Project

class CollectFromPomCommand {

    fun execute(input: Project): List<DependencyInformation> {

        input.logger.info("Checking Licenses from pom...")

        val licenses : MutableMap<String, List<String> > = mutableMapOf()

        val parser = PomParser()
        input.configurations.getByName("poms").forEach { pom ->
            val pomFile = pom.absoluteFile
            val parseLicense = parser.parseLicense(pomFile)

            input.logger.debug("pom={} - license={}", pom, licenses)
            licenses.put(pom.name.removeSuffix(".pom"), parseLicense)
        }

        val dependencyInformations = mutableListOf<DependencyInformation>()

        licenses.forEach { key, value ->
            dependencyInformations.add(DependencyInformation(key, value, "pom"))
        }

        return dependencyInformations
    }

}