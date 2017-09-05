package de.waschndolos.gradle.licenseguard

import org.gradle.api.Plugin
import org.gradle.api.Project


class LicenseGuardPlugin : Plugin<Project> {


    override fun apply(project: Project) {

        with(project) {

            val licenseGuardTask = tasks.create("licenseGuard", LicenseGuardTask::class.java)
        }

    }

}