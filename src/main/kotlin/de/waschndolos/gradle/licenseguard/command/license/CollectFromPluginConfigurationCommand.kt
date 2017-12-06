package de.waschndolos.gradle.licenseguard.command.license

import de.waschndolos.gradle.licenseguard.conversion.MapToDIConverter
import de.waschndolos.gradle.licenseguard.model.DependencyInformation

class CollectFromPluginConfigurationCommand(private val excludedDependencies: List<String>, private val licenseMap: Map<String, String>) {

    fun execute(input: List<DependencyInformation>): List<DependencyInformation> {

        val dependencyInformations = mutableListOf<DependencyInformation>()
        dependencyInformations.addAll(input)
        excludedDependencies.forEach { excludedDIName ->
            val foundDIs = mutableListOf<DependencyInformation>()

            input.forEach { di ->
                if (Regex(excludedDIName).matches(di.name)) {
                    foundDIs.add(di)
                }
            }
            foundDIs.forEach { foundDI ->
                dependencyInformations.remove(foundDI)
            }
        }

        MapToDIConverter().convert(licenseMap).forEach { manualDI ->
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

        return dependencyInformations
    }
}