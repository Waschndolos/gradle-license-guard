package de.waschndolos.gradle.licenseguard.command.license

import de.waschndolos.gradle.licenseguard.model.DependencyInformation
import de.waschndolos.gradle.licenseguard.parsing.ManifestParser
import org.gradle.api.Project

class CollectFromManifest(private val project: Project, private val configuration: String) {


     fun execute(input: Set<String>): List<DependencyInformation> {
        val licenses : MutableMap<String, List<String> > = mutableMapOf()

        val manifestParser = ManifestParser()
        project.configurations.getByName(configuration).forEach{ file ->

            if (input.contains(file.name.removeSuffix(".jar"))) {
                val license = manifestParser.parseManifest(file)
                licenses.put(file.name, license)
            }
        }

        val dependencyInformations = mutableListOf<DependencyInformation>()
        licenses.forEach { key, value ->
            dependencyInformations.add(DependencyInformation(key, value, "manifest"))
        }

        return dependencyInformations
    }
}