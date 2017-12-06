package de.waschndolos.gradle.licenseguard.command.report

import de.waschndolos.gradle.licenseguard.report.PdfReportCreator
import de.waschndolos.gradle.licenseguard.report.RtfReportCreator
import java.io.File

class CreateReportCommand {

    fun execute(outputFormat: List<String>, xmlFile: File, path: String) {

        if (outputFormat.contains("pdf")) {
            PdfReportCreator().createReport(xmlFile, path)
        }

        if (outputFormat.contains("rtf")) {
            RtfReportCreator().createReport(xmlFile, path)
        }
    }
}