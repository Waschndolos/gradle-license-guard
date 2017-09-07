package de.waschndolos.gradle.licenseguard.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "Dep")
data class DependencyInformation(var name: String, var license: List<String>, var source: String) {

    override fun toString(): String {
        return "DependencyInformation(name='$name', license=$license, source='$source')"
    }
}