package de.waschndolos.gradle.licenseguard.model

class DependencyInformation(var name: String, var license: List<String>, var source: String) {

    override fun toString(): String {
        return "DependencyInformation(name='$name', license=$license, source='$source')"
    }
}