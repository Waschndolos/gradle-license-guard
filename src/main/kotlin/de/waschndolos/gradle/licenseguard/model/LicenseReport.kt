package de.waschndolos.gradle.licenseguard.model

class LicenseReport(var projectName: String, var dependencyInformation: MutableList<DependencyInformation>) {

    override fun toString(): String {
        return "LicenseReport(dependencyInformation=$dependencyInformation)"
    }


}