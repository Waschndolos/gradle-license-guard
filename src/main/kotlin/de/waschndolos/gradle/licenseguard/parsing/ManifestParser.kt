package de.waschndolos.gradle.licenseguard.parsing

import org.apache.commons.io.FileUtils
import java.io.File
import java.util.jar.JarInputStream

class ManifestParser {

    fun parseManifest(file : File) : List<String> {

        val licenses : MutableList<String> = mutableListOf()
        val jarStream = JarInputStream(FileUtils.openInputStream(file))

        val manifest = jarStream.manifest

        val mainAttributes = manifest.mainAttributes
        if (mainAttributes != null) {
            val licenseInformation = mainAttributes.getValue("Bundle-License")
            if (licenseInformation != null && licenseInformation.isNotEmpty()) {
                licenses.add(licenseInformation)
            }
        }

        return licenses
    }

}