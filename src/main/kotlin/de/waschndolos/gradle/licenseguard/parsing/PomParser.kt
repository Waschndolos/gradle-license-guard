package de.waschndolos.gradle.licenseguard.parsing



import org.apache.maven.model.io.xpp3.MavenXpp3Reader
import java.io.ByteArrayInputStream
import java.io.File

class PomParser {

    fun parseLicense(pom: File) : List<String> {

        val mavenXpp3Reader = MavenXpp3Reader()
        val byteArrayInputStream = ByteArrayInputStream(pom.readBytes())
        val read = mavenXpp3Reader.read(byteArrayInputStream)

        val licenses : MutableList<String> = mutableListOf()

        read.licenses
                .filter { it.name != null }
                .mapTo(licenses) { it.name }

        return licenses
    }
}
