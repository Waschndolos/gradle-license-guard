package de.waschndolos.gradle.licenseguard.conversion

import de.waschndolos.gradle.licenseguard.model.DependencyInformation

class DependencyInformationsToHintConverter {

    fun convert(input : List<DependencyInformation> ) : String {

        var output = "licenseMap = [\n"
        input.sortedBy { dep -> dep.name }.forEach { dep ->
            output = output.plus("\"")
                    .plus(dep.name)
                    .plus("\" ")
                    .plus(":")
                    .plus(" \"\",")
                    .plus("\n")
        }
        return output.plus("]")

    }
}