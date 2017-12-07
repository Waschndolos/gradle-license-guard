package de.waschndolos.gradle.licenseguard.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

data class LicenseReport(val projectName: String?,
                         @JacksonXmlElementWrapper(useWrapping = false) val dependencyInformation: List<DependencyInformation>,
                         val base64EncodedImage: String,
                         val description: String) {

    override fun toString(): String {
        return "LicenseReport(dependencyInformation=$dependencyInformation)"
    }

}