package de.waschndolos.gradle.licenseguard.conversion

import de.waschndolos.gradle.licenseguard.model.DependencyInformation

class MapToDependencyInformationConverter {


    fun convert(input : Map<String, String>) : List<DependencyInformation> {

        val list : MutableList<DependencyInformation> = mutableListOf()

        input.entries.forEach { entry ->
            val module = entry.key
            val license = entry.value

            list.add(DependencyInformation(module, listOf(license), "manual"))
        }

        return list
    }
}