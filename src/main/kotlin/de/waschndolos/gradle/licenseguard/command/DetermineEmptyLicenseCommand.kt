package de.waschndolos.gradle.licenseguard.command

import de.waschndolos.gradle.licenseguard.model.DependencyInformation

class DetermineEmptyLicenseCommand {

    fun execute(input: List<DependencyInformation>): List<String> {
        val missingLicenses = mutableListOf<String>()

        input.filter{ it.license.isEmpty() }.forEach{ missingLicenses.add(it.name) }

        return missingLicenses
    }
}