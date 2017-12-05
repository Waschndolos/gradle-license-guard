package de.waschndolos.gradle.licenseguard.report

import org.apache.fop.apps.MimeConstants

class PdfReportCreator : ReportCreator() {

    override fun format(): String {
        return MimeConstants.MIME_PDF
    }

}
