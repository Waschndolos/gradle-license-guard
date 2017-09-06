package de.waschndolos.gradle.licenseguard

import org.gradle.api.Plugin
import org.gradle.api.Project


class LicenseGuardPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            tasks.create("licenseGuard", LicenseReportTask::class.java)
        }
    }
}