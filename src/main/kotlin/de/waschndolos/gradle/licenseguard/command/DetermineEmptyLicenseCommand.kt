package de.waschndolos.gradle.licenseguard.command

import de.waschndolos.gradle.licenseguard.model.DependencyInformation

class DetermineEmptyLicenseCommand {

    fun execute(input: List<DependencyInformation>): Set<String> {
        val missingLicenses = mutableSetOf<String>()

        input.filter{ it.license.isEmpty() }.forEach{ missingLicenses.add(it.name) }

        return missingLicenses
    }
}