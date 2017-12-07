package de.waschndolos.gradle.licenseguard.command.license

import de.waschndolos.gradle.licenseguard.conversion.MapToDIConverter
import de.waschndolos.gradle.licenseguard.model.DependencyInformation

class CollectFromPluginConfigurationCommand(private val excludedDependencies: List<String>, private val licenseMap: Map<String, String>) {

    fun execute(dependencyInformation: MutableList<DependencyInformation>) {

        excludedDependencies.forEach { excludedDIName ->
            val foundDIs = mutableListOf<DependencyInformation>()

            dependencyInformation.forEach { di ->
                if (Regex(excludedDIName).matches(di.name)) {
                    foundDIs.add(di)
                }
            }
            foundDIs.forEach { foundDI ->
                dependencyInformation.remove(foundDI)
            }
        }

        MapToDIConverter().convert(licenseMap).forEach { manualDI ->
            val foundDIs = mutableListOf<DependencyInformation>()
            dependencyInformation.forEach { di ->
                if (di.name.startsWith(manualDI.name)) {
                    foundDIs.add(di)
                }
            }
            foundDIs.forEach { foundDI ->
                dependencyInformation.remove(foundDI)
                dependencyInformation.add(manualDI)
            }

        }

    }
}