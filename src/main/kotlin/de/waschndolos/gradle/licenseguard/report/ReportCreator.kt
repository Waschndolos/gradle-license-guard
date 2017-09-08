package de.waschndolos.gradle.licenseguard.report

import org.apache.fop.apps.FopFactory
import org.apache.fop.apps.MimeConstants
import java.io.File
import java.io.FileOutputStream
import java.io.StringReader
import javax.xml.transform.TransformerFactory
import javax.xml.transform.sax.SAXResult
import javax.xml.transform.stream.StreamSource

abstract class ReportCreator {

    fun createReport(xmlReport: File, outputPath: String) {

        val xsltFile = javaClass.classLoader.getResource("license-report.xsl").readText()

        println("xmlReport: " + xmlReport)
        val streamSource = StreamSource(xmlReport)

        val fopFactory = FopFactory.newInstance(File(".").toURI())

        val foUserAgent = fopFactory.newFOUserAgent()

        val outputStream = FileOutputStream(outputPath + getEnding(format()))

        outputStream.use { outputStream ->
            val fop = fopFactory.newFop(format(), foUserAgent, outputStream)

            val factory = TransformerFactory.newInstance()
            val transformer = factory.newTransformer(StreamSource(StringReader(xsltFile)))

            val res = SAXResult(fop.defaultHandler)

            transformer.transform(streamSource, res)
        }

    }

    private fun getEnding(mimeTypeString : String) : String {
        if (MimeConstants.MIME_RTF == mimeTypeString) {
            return ".rtf"
        } else if (MimeConstants.MIME_PDF == mimeTypeString) {
            return ".pdf"
        }
        return ".dat"
    }

    abstract fun format() : String
}
