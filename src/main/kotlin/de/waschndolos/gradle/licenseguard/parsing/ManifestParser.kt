package de.waschndolos.gradle.licenseguard.parsing

import org.apache.commons.io.FileUtils
import java.io.File
import java.util.jar.JarInputStream

class ManifestParser {

    fun parseManifest(file : File) : List<String> {

        val licenses : MutableList<String> = mutableListOf<String>()
        val jarStream = JarInputStream(FileUtils.openInputStream(file))

        val manifest = jarStream.manifest

        val licenseInformation = manifest.mainAttributes.getValue("Bundle-License").toString()
        if (licenseInformation.isNotEmpty()) {
            licenses.add(licenseInformation)
        }

        return licenses
    }

}