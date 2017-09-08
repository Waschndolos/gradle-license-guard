package de.waschndolos.gradle.licenseguard.report

import org.apache.fop.apps.MimeConstants

class RtfReportCreator : ReportCreator()  {

    override fun format(): String {
        return MimeConstants.MIME_RTF
    }

}
