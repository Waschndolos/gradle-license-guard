package de.waschndolos.gradle.licenseguard.command.license

import de.waschndolos.gradle.licenseguard.model.DependencyInformation
import de.waschndolos.gradle.licenseguard.parsing.ManifestParser
import org.gradle.api.Project

class CollectFromManifest(private val project: Project, private val configuration: String) {


     fun execute(input: List<String>, dependencyInformations: MutableList<DependencyInformation>) {
        val licenses : MutableMap<String, List<String> > = mutableMapOf()

        val manifestParser = ManifestParser()
        project.configurations.getByName(configuration).forEach{ file ->

            if (input.contains(file.name)) {
                val license = manifestParser.parseManifest(file)
                licenses.put(file.name, license)
            }
        }

        licenses.forEach { key, value ->
            val existing = dependencyInformations.find { it.name.equals(key) }
            if (existing != null) {
                dependencyInformations.remove(existing)
            }
            dependencyInformations.add(DependencyInformation(key, value, "manifest"))
        }
    }
}