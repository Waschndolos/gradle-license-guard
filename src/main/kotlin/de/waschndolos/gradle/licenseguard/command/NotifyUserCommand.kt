package de.waschndolos.gradle.licenseguard.command

import de.waschndolos.gradle.licenseguard.conversion.DIToConfigurationHintConverter
import de.waschndolos.gradle.licenseguard.model.DependencyInformation
import org.gradle.api.Project

class NotifyUserCommand {

    fun execute(project: Project, dependencyInformations: List<DependencyInformation>) {

        val noLicensesFound = dependencyInformations.filter { dep -> dep.license.isEmpty() }

        if (noLicensesFound.isNotEmpty()) {
            project.logger.warn("You should add this to your task configuration " +
                    " and fill in licenses manually to ensure the report is filled correctly with the next run!")
            project.logger.warn("------------------------")
            project.logger.warn(DIToConfigurationHintConverter().convert(noLicensesFound))
            project.logger.warn("------------------------")
        }

    }
}